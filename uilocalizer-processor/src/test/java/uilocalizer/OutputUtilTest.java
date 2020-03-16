/*
 * #%L
 * UI Localizer
 * %%
 * Copyright (C) 2015 - 2019 Devexperts, LLC
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

package uilocalizer;

import com.devexperts.uilocalizer.OutputUtil;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class OutputUtilTest {

    private static List<Map.Entry<String,String>> createTestList() {
        List<Map.Entry<String,String>> testList = new ArrayList<>();
        testList.add(new AbstractMap.SimpleEntry<>("testcom.specChar", "#!= \n!"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.usualString", "Test string."));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.endLine", "\n"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.exclamPt", "!"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.equals", "="));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.sharpString", "#"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.doubleBackSlash", "\\\\"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.backSlash", "\\"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.spaceInTheBegining", " Is ok"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.doubleSpaceInTheBegining", "  Is ok"));
        testList.add(new AbstractMap.SimpleEntry<>("testcom.at", "®"));
        return Collections.unmodifiableList(testList);
    }

    @Test
    public void generatePropertyFilesTest() throws Exception {
        List<Map.Entry<String,String>> testList = createTestList();
        OutputUtil.generatePropertyFiles(Paths.get("."), testList);
        Properties properties = loadProperties();
        testList.forEach(e -> checkPropertyExistence(properties, e.getKey(), e.getValue()));

        /*test property addition*/
        List<Map.Entry<String,String>> addList = new ArrayList<>();
        addList.add(new AbstractMap.SimpleEntry<>("testcom.addProperty", "added"));
        OutputUtil.generatePropertyFiles(Paths.get("."), addList);
        Properties addedProperties = loadProperties();
        assertEquals("added", addedProperties.getProperty("addProperty"));
        testList.forEach(e -> checkPropertyExistence(addedProperties, e.getKey(), e.getValue()));

        /*test property update*/
        List<Map.Entry<String,String>> updateList = new ArrayList<>();
        updateList.add(new AbstractMap.SimpleEntry<>("testcom.addProperty", "updated"));
        OutputUtil.generatePropertyFiles(Paths.get("."), updateList);
        Properties updatedProperties = loadProperties();
        assertEquals("updated", updatedProperties.getProperty("addProperty"));

        /*test original properties exist after update*/
        List<Map.Entry<String,String>> resultList = new ArrayList<>();
        resultList.addAll(testList);
        resultList.addAll(updateList);
        resultList.forEach(e -> checkPropertyExistence(updatedProperties, e.getKey(), e.getValue()));
    }

    private Properties loadProperties() throws IOException {
        InputStream propertiesInput = new FileInputStream("testcom.properties");
        Properties properties = new Properties();
        properties.load(propertiesInput);
        propertiesInput.close();
        return properties;
    }

    private static void checkPropertyExistence(Properties properties, String key, String value) {
        assertEquals(value, properties.getProperty(key.substring(key.indexOf('.') + 1, key.length())));
    }

    @AfterClass
    public static void clean() {
        File dir = new File("testcom.properties");
        dir.delete();
    }
}
