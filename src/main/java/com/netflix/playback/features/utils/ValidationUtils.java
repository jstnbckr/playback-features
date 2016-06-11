package com.netflix.playback.features.utils;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * This class is just temporary for demonstration purpose of validation logics.
 */
public class ValidationUtils {

    private static final Set<String> COUNTRIES = ImmutableSet.of("CN", "US");

    public static String validateCountryKey(String country) {
        if (!COUNTRIES.contains(country)) {
            throw new IllegalArgumentException("Invalid country " + country);
        }
        return country;
    }

    public static String validateViewableId(String viewableId) {
        if (!viewableId.startsWith("viewable")) {
            throw new IllegalArgumentException("Invalid viewableId " + viewableId);
        }
        return viewableId;
    }
}