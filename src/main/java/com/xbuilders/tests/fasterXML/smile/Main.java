package com.xbuilders.tests.fasterXML.smile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.xbuilders.tests.fasterXML.smile.custom.RecordDeserializer;
import com.xbuilders.tests.fasterXML.smile.custom.RecordSerializer;
import java.io.ByteArrayOutputStream;

public class Main {


    public static void main(String[] args) {
        SmileFactory smileFactory = new SmileFactory();
        //set flags
        smileFactory.enable(SmileGenerator.Feature.ENCODE_BINARY_AS_7BIT);
        smileFactory.enable(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES);

        //create new object mapper based on factory
        ObjectMapper objectMapper = new ObjectMapper(smileFactory);
//        SmileMapper smileMapper = new SmileMapper(smileFactory); //You could also use this instead of ObjectMapper

        // Create a module to register custom serializer and deserializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(smileObject.class, new RecordSerializer()); // Register the custom serializer
        module.addDeserializer(smileObject.class, new RecordDeserializer()); // Register the custom deserializer

        // Register the module with the ObjectMapper
        objectMapper.registerModule(module);

        // Create an example object
        smileObject myObject = new smileObject();
        myObject.setName("John");
        myObject.setValue(25);

        // Serialize the object to a JSON string
        try {
            System.out.println("Original Object: " + myObject);

            // Use ByteArrayOutputStream (byte-based)
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Serialize the object to JSON using the ByteArrayOutputStream (byte-based)
            objectMapper.writeValue(byteArrayOutputStream, myObject);
            System.out.println("\n\nSerialized JSON: " + new String(byteArrayOutputStream.toByteArray()));

            // Deserialize the JSON string back into the object
            smileObject deserializedObject = objectMapper.readValue(byteArrayOutputStream.toByteArray(), smileObject.class);
            System.out.println("Deserialized Object: " + deserializedObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}