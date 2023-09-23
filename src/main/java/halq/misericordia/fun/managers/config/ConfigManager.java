package halq.misericordia.fun.managers.config;

import com.google.gson.*;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.core.settingcore.Setting;
import halq.misericordia.fun.executor.settings.*;
import halq.misericordia.fun.managers.friend.FriendManager;
import halq.misericordia.fun.managers.setting.SettingManager;
import halq.misericordia.fun.managers.module.ModuleManager;
import halq.misericordia.fun.managers.command.CommandManager;
import net.minecraftforge.fml.common.FMLLog;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author accessmodifier364
 * @since 24-Nov-2021
 */

@SuppressWarnings("all")
public class ConfigManager {

    //TODO: make load colors work

    private static final String fileName = "Misericordia/";
    private static final String moduleName = "modules/";
    public static ConfigManager INSTANCE;

    public void saveConfigs() {

        try {
            if (!Files.exists(Paths.get(fileName))) {
                Files.createDirectories(Paths.get(fileName));
            } else if (!Files.exists(Paths.get(fileName + moduleName))) {
                Files.createDirectories(Paths.get(fileName + moduleName));
            }

            saveModules();
            saveEnabledModules();
            saveModuleKeybinds();
            saveCommandPrefix();
            saveFriendsList();
            //saveAutoGG();
        } catch (IOException ignored) {
        }
    }

    private void saveModules() throws IOException {
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            try {
                saveModuleDirect(module);
            } catch (IOException ignored) {
            }
        }
    }

    private void saveModuleDirect(Module module) throws IOException {
        registerFiles(fileName + moduleName, module.getName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + moduleName + module.getName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", new JsonPrimitive(module.getName()));

        for (Setting setting : SettingManager.INSTANCE.getSettingsInModule(module)) {
            if (setting instanceof SettingBoolean) {
                settingObject.add(setting.getName(), new JsonPrimitive(((SettingBoolean) setting).getValue()));
            } else if (setting instanceof SettingColor) {

            } else if (setting instanceof SettingDouble) {
                settingObject.add(setting.getName(), new JsonPrimitive(((SettingDouble) setting).getValue()));
            } else if (setting instanceof SettingInteger) {
                settingObject.add(setting.getName(), new JsonPrimitive(((SettingInteger) setting).getValue()));
            } else if (setting instanceof SettingMode) {
                settingObject.add(setting.getName(), new JsonPrimitive(((SettingMode) setting).getValue()));
            } else if (setting instanceof SettingString) {
                settingObject.add(setting.getName(), new JsonPrimitive(((SettingString) setting).getValue()));
            }
        }
        moduleObject.add("Settings", settingObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveEnabledModules() throws IOException {
        registerFiles(fileName, "Toggle");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + "Toggle" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for (Module module : ModuleManager.INSTANCE.getModules()) {

            enabledObject.add(module.getName(), new JsonPrimitive(module.isEnabled()));
        }
        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveModuleKeybinds() throws IOException {

        registerFiles(fileName, "Bind");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + "Bind" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject bindObject = new JsonObject();

        for (Module module : ModuleManager.INSTANCE.getModules()) {

            bindObject.add(module.getName(), new JsonPrimitive(module.getKey()));
        }
        moduleObject.add("Modules", bindObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveCommandPrefix() throws IOException {

        registerFiles(fileName, "CommandPrefix");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + "CommandPrefix" + ".json"), StandardCharsets.UTF_8);
        JsonObject prefixObject = new JsonObject();

        prefixObject.add("Prefix", new JsonPrimitive(CommandManager.INSTANCE.getPrefix()));
        String jsonString = gson.toJson(new JsonParser().parse(prefixObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveFriendsList() throws IOException {

        registerFiles(fileName, "Friends");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + "Friends" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray friendArray = new JsonArray();

        for (FriendManager.Friend friend : FriendManager.friends) {
            friendArray.add(friend.getUsername());
        }
        mainObject.add("Friends", friendArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    public void loadConfigs() {

        try {
            loadModules();
            loadEnabledModules();
            loadModuleKeybinds();

            loadCommandPrefix();
            loadFriendsList();
        } catch (Exception ignored) {
        }
    }

    private void loadModules() {
        String moduleLocation = fileName + moduleName;

        for (Module module : ModuleManager.INSTANCE.getModules()) {
            try {
                loadModuleDirect(moduleLocation, module);
            } catch (IOException ignored) {
                System.out.println(module.getName());
            }
        }
    }

    private void loadModuleDirect(String moduleLocation, Module module) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
        JsonObject moduleObject;
        try {
            moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        } catch (IllegalStateException e) {
            return;
        }

        if (moduleObject.get("Module") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();

        for (Setting setting : SettingManager.INSTANCE.getSettingsInModule(module)) {
            JsonElement dataObject = settingObject.get(setting.getName());
            try {
                if (dataObject != null) {
                    if (setting instanceof SettingBoolean) {
                        setting.setValue(dataObject.getAsBoolean());
                    } else if (setting instanceof SettingColor) {
                        JsonElement colorRedObject = settingObject.get(setting.getName() + "_R");
                        JsonElement colorGreenObject = settingObject.get(setting.getName() + "_G");
                        JsonElement colorBlueObject = settingObject.get(setting.getName() + "_B");
                        JsonElement colorAlphaObject = settingObject.get(setting.getName() + "_A");
                        ((SettingColor) setting).setRed(colorRedObject.getAsInt());
                        ((SettingColor) setting).setGreen(colorGreenObject.getAsInt());
                        ((SettingColor) setting).setBlue(colorBlueObject.getAsInt());
                        ((SettingColor) setting).setAlpha(colorAlphaObject.getAsInt());
                    } else if (setting instanceof SettingDouble) {
                        setting.setValue(dataObject.getAsDouble());
                    } else if (setting instanceof SettingInteger) {
                        setting.setValue(dataObject.getAsInt());
                    } else if (setting instanceof SettingMode) {
                        setting.setValue(dataObject.getAsString());
                    } else if (setting instanceof SettingString) {
                        setting.setValue(dataObject.getAsString());
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
        inputStream.close();
    }

    private void loadEnabledModules() throws IOException {
        String toggleLocation = fileName;

        if (!Files.exists(Paths.get(toggleLocation + "Toggle" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(toggleLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                if (dataObject.getAsBoolean()) {
                    try {
                        module.setEnabled();
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }
        inputStream.close();
    }

    private void loadModuleKeybinds() throws IOException {
        String bindLocation = fileName;

        if (!Files.exists(Paths.get(bindLocation + "Bind" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(bindLocation + "Bind" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setKey(dataObject.getAsInt());
            }
        }
        inputStream.close();
    }

    private void loadCommandPrefix() throws IOException {
        String prefixLocation = fileName;

        if (!Files.exists(Paths.get(prefixLocation + "CommandPrefix" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(prefixLocation + "CommandPrefix" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Prefix") == null) {
            return;
        }

        JsonElement prefixObject = mainObject.get("Prefix");

        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            CommandManager.INSTANCE.setPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }

    private void loadFriendsList() throws IOException {
        String friendLocation = fileName;

        if (!Files.exists(Paths.get(friendLocation + "Friends" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "Friends" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Friends") == null) {
            return;
        }

        JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();

        friendObject.forEach(object -> FriendManager.friends.add(FriendManager.getFriendObject(object.getAsString())));
        inputStream.close();
    }

    private void registerFiles(String location, String name) throws IOException {

        if (Files.exists(Paths.get(location + name + ".json"))) {
            File file = new File(location + name + ".json");
            file.delete();
        }
        Files.createFile(Paths.get(location + name + ".json"));

    }

    private void saveSettingColor(SettingColor setting, JsonObject settingObject) {
        settingObject.addProperty(setting.getName() + "_R", setting.getValue().getRed());
        settingObject.addProperty(setting.getName() + "_G", setting.getValue().getGreen());
        settingObject.addProperty(setting.getName() + "_B", setting.getValue().getBlue());
        settingObject.addProperty(setting.getName() + "_A", setting.getValue().getAlpha());
    }
}