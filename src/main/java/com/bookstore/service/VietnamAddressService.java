package com.bookstore.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VietnamAddressService - Service for Vietnam Address API
 * Provides provinces, districts, and wards data with in-memory caching
 * API: https://provinces.open-api.vn/api/
 */
public class VietnamAddressService {

    private static final Logger LOGGER = Logger.getLogger(VietnamAddressService.class.getName());
    private static final String API_BASE_URL = "https://provinces.open-api.vn/api";

    // In-memory cache with ConcurrentHashMap for thread-safety
    private static final Map<String, List<Province>> PROVINCE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, List<District>> DISTRICT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, List<Ward>> WARD_CACHE = new ConcurrentHashMap<>();

    // Cache keys
    private static final String ALL_PROVINCES_KEY = "ALL_PROVINCES";

    private final Gson gson = new Gson();

    /**
     * Get all provinces (cached)
     */
    public List<Province> getAllProvinces() {
        // Check cache first
        if (PROVINCE_CACHE.containsKey(ALL_PROVINCES_KEY)) {
            LOGGER.log(Level.INFO, "Returning provinces from cache");
            return PROVINCE_CACHE.get(ALL_PROVINCES_KEY);
        }

        // Fetch from API
        try {
            String url = API_BASE_URL + "/p/";
            String response = makeHttpRequest(url);

            List<Province> provinces = gson.fromJson(response,
                    new TypeToken<List<Province>>() {
                    }.getType());

            // Sort by name
            provinces.sort(Comparator.comparing(Province::getName));

            // Cache the result
            PROVINCE_CACHE.put(ALL_PROVINCES_KEY, provinces);

            LOGGER.log(Level.INFO, "Fetched and cached {0} provinces", provinces.size());
            return provinces;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch provinces", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get districts by province code (cached)
     */
    public List<District> getDistrictsByProvince(String provinceCode) {
        // Check cache first
        String cacheKey = "PROVINCE_" + provinceCode;
        if (DISTRICT_CACHE.containsKey(cacheKey)) {
            LOGGER.log(Level.INFO, "Returning districts from cache for province: {0}", provinceCode);
            return DISTRICT_CACHE.get(cacheKey);
        }

        // Fetch from API
        try {
            String url = API_BASE_URL + "/p/" + provinceCode + "?depth=2";
            String response = makeHttpRequest(url);

            ProvinceDetail provinceDetail = gson.fromJson(response, ProvinceDetail.class);
            List<District> districts = provinceDetail.getDistricts();

            // Sort by name
            if (districts != null) {
                districts.sort(Comparator.comparing(District::getName));

                // Cache the result
                DISTRICT_CACHE.put(cacheKey, districts);

                LOGGER.log(Level.INFO, "Fetched and cached {0} districts for province: {1}",
                        new Object[] { districts.size(), provinceCode });
            }

            return districts != null ? districts : new ArrayList<>();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch districts for province: " + provinceCode, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get wards by district code (cached)
     */
    public List<Ward> getWardsByDistrict(String districtCode) {
        // Check cache first
        String cacheKey = "DISTRICT_" + districtCode;
        if (WARD_CACHE.containsKey(cacheKey)) {
            LOGGER.log(Level.INFO, "Returning wards from cache for district: {0}", districtCode);
            return WARD_CACHE.get(cacheKey);
        }

        // Fetch from API
        try {
            String url = API_BASE_URL + "/d/" + districtCode + "?depth=2";
            String response = makeHttpRequest(url);

            DistrictDetail districtDetail = gson.fromJson(response, DistrictDetail.class);
            List<Ward> wards = districtDetail.getWards();

            // Sort by name
            if (wards != null) {
                wards.sort(Comparator.comparing(Ward::getName));

                // Cache the result
                WARD_CACHE.put(cacheKey, wards);

                LOGGER.log(Level.INFO, "Fetched and cached {0} wards for district: {1}",
                        new Object[] { wards.size(), districtCode });
            }

            return wards != null ? wards : new ArrayList<>();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch wards for district: " + districtCode, e);
            return new ArrayList<>();
        }
    }

    /**
     * Clear all caches (useful for refresh)
     */
    public void clearCache() {
        PROVINCE_CACHE.clear();
        DISTRICT_CACHE.clear();
        WARD_CACHE.clear();
        LOGGER.log(Level.INFO, "All address caches cleared");
    }

    /**
     * Make HTTP GET request
     */
    private String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP GET Request Failed with Error code : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    // ==================== MODEL CLASSES ====================

    /**
     * Province model
     */
    public static class Province {
        private String code;
        private String name;
        private String codename;
        private String division_type;
        private Integer phone_code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCodename() {
            return codename;
        }

        public void setCodename(String codename) {
            this.codename = codename;
        }

        public String getDivision_type() {
            return division_type;
        }

        public void setDivision_type(String division_type) {
            this.division_type = division_type;
        }

        public Integer getPhone_code() {
            return phone_code;
        }

        public void setPhone_code(Integer phone_code) {
            this.phone_code = phone_code;
        }
    }

    /**
     * District model
     */
    public static class District {
        private String code;
        private String name;
        private String codename;
        private String division_type;
        private String province_code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCodename() {
            return codename;
        }

        public void setCodename(String codename) {
            this.codename = codename;
        }

        public String getDivision_type() {
            return division_type;
        }

        public void setDivision_type(String division_type) {
            this.division_type = division_type;
        }

        public String getProvince_code() {
            return province_code;
        }

        public void setProvince_code(String province_code) {
            this.province_code = province_code;
        }
    }

    /**
     * Ward model
     */
    public static class Ward {
        private String code;
        private String name;
        private String codename;
        private String division_type;
        private String district_code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCodename() {
            return codename;
        }

        public void setCodename(String codename) {
            this.codename = codename;
        }

        public String getDivision_type() {
            return division_type;
        }

        public void setDivision_type(String division_type) {
            this.division_type = division_type;
        }

        public String getDistrict_code() {
            return district_code;
        }

        public void setDistrict_code(String district_code) {
            this.district_code = district_code;
        }
    }

    /**
     * Province detail with districts
     */
    private static class ProvinceDetail {
        private String code;
        private String name;
        private List<District> districts;

        public List<District> getDistricts() {
            return districts;
        }
    }

    /**
     * District detail with wards
     */
    private static class DistrictDetail {
        private String code;
        private String name;
        private List<Ward> wards;

        public List<Ward> getWards() {
            return wards;
        }
    }
}
