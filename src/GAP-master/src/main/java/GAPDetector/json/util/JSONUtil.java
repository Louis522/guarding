package GAPDetector.json.util;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class JSONUtil {

    private static final Gson GSON_READER = new GsonBuilder().create();
    private static final Gson GSON_WRITER = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


    public static <T> T fromJson(File file, Class<T> clz) throws FileNotFoundException {
        JsonReader reader = GSON_READER.newJsonReader(new FileReader(file));
        return GSON_READER.fromJson(reader, clz);
    }


    public static boolean toJson(Object obj, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + ".json";

        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            System.out.println("Writing the " + obj.toString() + " result...");
            try (Writer writer = new FileWriter(file)) {
                GSON_WRITER.toJson(obj, writer);
            }


        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return flag;
    }

    public static boolean toJson(Object obj, String filePath, String fileName, String outputMode) {
        boolean flag = true;
        String fullPath = filePath + File.separator + fileName + ".json";
        try {
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            // Define the fields to output based on the selected output mode
            List<String> excludedFields = new ArrayList<>();
            if (Objects.equals(outputMode, "detailed")) {
                List<String> idFields = Arrays.asList("parentId");
                excludedFields.addAll(idFields);


            } else if (outputMode.equals("benchmark")) {
                List<String> idFields = Arrays.asList("id", "fromEntityId", "toEntityId", "parentId", "fromParentId", "toParentId");
                List<String> modifierFields = Arrays.asList("modifier", "fromEntityModifier", "toEntityModifier");
                List<String> typeFields = Arrays.asList("modifier", "fromEntityType", "toEntityType", "intrusiveType");
                List<String> countFields = Arrays.asList("dependencyRelationCountTotal", "cyclicDependencyModulesIsIntrusiveCount");
                List<String> modeFields = Arrays.asList("mode", "outDegree", "dependencyRelationCountFromSrcToAllDest", "dependencyRelationCount");
                List<String> hierarchyFields = Arrays.asList("hierarchyPaths");
                List<String> tempFields = Arrays.asList("isIntrusive", "cyclicDependencyModulesIsIntrusive", "count");

                excludedFields.addAll(idFields);
                excludedFields.addAll(modifierFields);
                excludedFields.addAll(typeFields);
                excludedFields.addAll(countFields);
                excludedFields.addAll(modeFields);
                excludedFields.addAll(hierarchyFields);
                excludedFields.addAll(tempFields);

            } else {
                System.err.println("Invalid output mode");
                return false;
            }

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                            return excludedFields.contains(fieldAttributes.getName());
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> aClass) {
                            return false;
                        }
                    })
                    .create();

            // Convert the test object to JSON using the custom Gson instance
            String json = gson.toJson(obj);
            System.out.println("Writing the " + obj + " result...");


            try (Writer writer = new FileWriter(file)) {
                gson.toJson(obj, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }


        // 返回是否成功的标记
        return flag;
    }

    public static boolean outputCSVFile(StringWriter writer, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;
        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + ".csv";
        try {
            //如果文件不存在则创建
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            FileWriter fileWritter = new FileWriter(file);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(writer.toString());
            bufferWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void writeStringToFile(String str, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(str);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeCSVStringToFile(String str, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}