package com.bookstore.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
public class CloudinaryUtil {
    public static final Cloudinary cloudinary = new Cloudinary(
            ObjectUtils.asMap(
                    "cloud_name", "dbqaczv3a",
                    "api_key", "768828759798912",
                    "api_secret", "r30HokAMMQpDNxPmx7vuCn2gG40",
                    "secure", true
            )
    );
}
