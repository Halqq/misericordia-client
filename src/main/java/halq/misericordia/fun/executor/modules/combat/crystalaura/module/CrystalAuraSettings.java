package halq.misericordia.fun.executor.modules.combat.crystalaura.module;

import halq.misericordia.fun.executor.settings.SettingCategory;

/**
 * @author Halq
 * @since 16/06/2023 at 19:30
 */

public class CrystalAuraSettings {

    public static void caSettings() {
        SettingCategory cat = CrystalAuraModule.INSTANCE.settings;
        CrystalAuraModule ca = CrystalAuraModule.INSTANCE;

        /** ------------------------------------  PLACE  ---------------------------------------**/
        ca.place.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.multiPlace.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.maxDmg.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.minDmg.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.minHealth.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.ppt.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.placeMode.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.placeRange.setVisible(cat.getValue().equalsIgnoreCase("Place"));
        ca.playerRange.setVisible(cat.getValue().equalsIgnoreCase("Place"));

        /** ------------------------------------  BREAK  ---------------------------------------**/
        ca.breakCrystal.setVisible(cat.getValue().equalsIgnoreCase("Break"));
        ca.attackPredict.setVisible(cat.getValue().equalsIgnoreCase("Break"));
        ca.apt.setVisible(cat.getValue().equalsIgnoreCase("Break"));
        ca.breakMode.setVisible(cat.getValue().equalsIgnoreCase("Break"));
        ca.breakRange.setVisible(cat.getValue().equalsIgnoreCase("Break"));

        /** ------------------------------------  AUTOSWITCH  ---------------------------------**/
        ca.autoSwitch.setVisible(cat.getValue().equalsIgnoreCase("AutoSwitch"));
        ca.autoSwitchMode.setVisible(cat.getValue().equalsIgnoreCase("AutoSwitch"));

        /** ------------------------------------  MULTITHREADING  ------------------------------**/
        ca.multiThread.setVisible(cat.getValue().equalsIgnoreCase("MultiThread"));
        ca.multiThreadDelay.setVisible(cat.getValue().equalsIgnoreCase("MultiThread"));
        ca.multiThreadValue.setVisible(cat.getValue().equalsIgnoreCase("MultiThread"));

        /** ------------------------------------  MISC  ---------------------------------------**/
        ca.pauseOnXp.setVisible(cat.getValue().equalsIgnoreCase("Misc"));
        ca.pauseOnGap.setVisible(cat.getValue().equalsIgnoreCase("Misc"));
        ca.handAnimations.setVisible(cat.getValue().equalsIgnoreCase("Misc"));

        /** ------------------------------------  ROTATIONS  -------------------------------------**/
        ca.rotations.setVisible(cat.getValue().equalsIgnoreCase("Rotations"));
        ca.rotateMode.setVisible(cat.getValue().equalsIgnoreCase("Rotations"));

        /** ------------------------------------  RENDER  -------------------------------------**/
        ca.render.setVisible(cat.getValue().equalsIgnoreCase("Render"));
        ca.red.setVisible(cat.getValue().equalsIgnoreCase("Render"));
        ca.green.setVisible(cat.getValue().equalsIgnoreCase("Render"));
        ca.blue.setVisible(cat.getValue().equalsIgnoreCase("Render"));
        ca.alpha.setVisible(cat.getValue().equalsIgnoreCase("Render"));
    }
}
