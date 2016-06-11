package com.netflix.playback.features.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * This class is just temporary for demonstration purpose of validtion logics.
 */
public class ValidationUtils {

    private static final Set<String> COUNTRIES = ImmutableSet.of("CN", "US");

    public static void validateCountryKey(String country) {
        if (!COUNTRIES.contains(country)) {
            throw new IllegalArgumentException("Invalid country " + country);
        }
    }

    public static void validateViewableId(String viewableId) {
        if (!viewableId.startsWith("viewable")) {
            throw new IllegalArgumentException("Invalid viewableId " + viewableId);
        }
    }
}