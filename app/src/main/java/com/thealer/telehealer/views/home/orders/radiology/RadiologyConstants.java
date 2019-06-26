package com.thealer.telehealer.views.home.orders.radiology;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 10,December,2018
 */
public class RadiologyConstants {

    public static final int TYPE_SECTION = 1;
    public static final int TYPE_HEADER = 2;
    public static final int TYPE_ITEM = 3;
    public static final int TYPE_SUB_ITEM = 4;
    public static final int TYPE_SELECT_ALL = 5;
    public static final int TYPE_RL = 6;
    public static final int TYPE_INPUT = 7;

    public static final int RL_TYPE_R = 0;
    public static final int RL_TYPE_L = 1;
    public static final int RL_TYPE_BOTH = 2;

    public static final ArrayList<String> rlType = new ArrayList<>(Arrays.asList("R", "L", "Both"));
    public static final ArrayList<Integer> rlTypeValue = new ArrayList<>(Arrays.asList(RL_TYPE_R, RL_TYPE_L, RL_TYPE_BOTH));


    public static final String HEADER_CT_ANGIOGRAPH = "CT ANGIOGRAPHY**";
    public static final String HEADER_CT_SCANS = "CT SCANS";
    public static final String HEADER_MR_ANGIOGRAPHY = "MR ANGIOGRAPHY";
    public static final String HEADER_MRI = "MRI";
    public static final String HEADER_NUCLEAR_MEDICINE = "NUCLEAR MEDICINE";
    public static final String HEADER_RADIOGRAPHY = "RADIOGRAPHY";
    public static final String HEADER_ULTRASONIC = "ULTRASOUND";

    private List<RadiologyListModel> radiologyModelList = new ArrayList<>();
    private RadiologyListModel radiologyListModel;

    private final List<String> sectionsList = new ArrayList<>(Arrays.asList(HEADER_CT_ANGIOGRAPH, HEADER_CT_SCANS, HEADER_MR_ANGIOGRAPHY, HEADER_MRI, HEADER_NUCLEAR_MEDICINE, HEADER_RADIOGRAPHY, HEADER_ULTRASONIC));
    private HashMap<String, List<String>> sectionMap = new HashMap<>();
    private int id = 0;

    private HashMap<String, List<String>> getSectionMap() {

        sectionMap.put(HEADER_CT_ANGIOGRAPH, getCtAngiographyList());
        sectionMap.put(HEADER_CT_SCANS, getCtScanList());
        sectionMap.put(HEADER_MR_ANGIOGRAPHY, getMrAngiographyList());
        sectionMap.put(HEADER_MRI, getMriList());
        sectionMap.put(HEADER_NUCLEAR_MEDICINE, getNuclearMedicineList());
        sectionMap.put(HEADER_RADIOGRAPHY, getRadiographyList());
        sectionMap.put(HEADER_ULTRASONIC, getUltrasoundList());

        return sectionMap;
    }

    private HashMap<String, List<String>> headerMap;

    public List<RadiologyListModel> getRadiologyListModel() {
        for (int i = 0; i < getSectionMap().size(); i++) {
            addItemToModelList(null, null, null, sectionsList.get(i), TYPE_SECTION);
            generateListModel(sectionsList.get(i), null, null, getSectionMap().get(sectionsList.get(i)));
        }
        return radiologyModelList;
    }

    public List<RadiologyListModel> getRadiologyListModel(String searchQuery) {
        List<RadiologyListModel> resultList = new ArrayList<>();
        List<RadiologyListModel> listModels = getRadiologyListModel();

        List<String> searchHeaders = new ArrayList<>();
        for (int i = 0; i < listModels.size(); i++) {
            if (listModels.get(i).getItem() != null && listModels.get(i).getItem().toLowerCase().contains(searchQuery)) {
                if (!searchHeaders.contains(listModels.get(i).getHeader())) {
                    searchHeaders.add(listModels.get(i).getHeader());
                }
                resultList.add(listModels.get(i));
            }
        }

        List<String> addedSections = new ArrayList<>();
        List<String> addedHeaders = new ArrayList<>();
        List<RadiologyListModel> returnResultList = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            if (!addedSections.contains(resultList.get(i).getSection())) {
                addedSections.add(resultList.get(i).getSection());
                RadiologyListModel radiologyListModel = new RadiologyListModel();
                radiologyListModel.setType(TYPE_SECTION);
                radiologyListModel.setSection(resultList.get(i).getSection());
                returnResultList.add(radiologyListModel);
            }

            if (resultList.get(i).getHeader() != null && !resultList.get(i).getHeader().isEmpty()) {
                if (!addedHeaders.contains(resultList.get(i).getHeader())) {
                    addedHeaders.add(resultList.get(i).getHeader());
                    RadiologyListModel radiologyListModel = new RadiologyListModel();
                    radiologyListModel.setType(TYPE_HEADER);
                    radiologyListModel.setSection(resultList.get(i).getSection());
                    radiologyListModel.setHeader(resultList.get(i).getHeader());
                    returnResultList.add(radiologyListModel);
                }
            }
            returnResultList.add(resultList.get(i));
        }
        return returnResultList;
    }

    private void generateListModel(String section, String header, String selectAll, List<String> list) {

        HashMap<String, List<String>> mapList = getMapList(section);
        HashMap<String, Integer> itemTypeMap = getItemTypeMap(section);

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {

                if (itemTypeMap != null && itemTypeMap.containsKey(list.get(i))) {

                    addItemToModelList(section, header, selectAll, list.get(i), itemTypeMap.get(list.get(i)));

                    switch (itemTypeMap.get(list.get(i))) {
                        case TYPE_HEADER:
                            if (mapList != null) {
                                generateListModel(section, list.get(i), selectAll, mapList.get(list.get(i)));
                            }
                            break;
                        case TYPE_SELECT_ALL:
                            if (mapList != null) {
                                generateListModel(section, header, list.get(i), mapList.get(list.get(i)));
                            }
                            break;
                        case TYPE_SUB_ITEM:
                            if (mapList != null) {
                                generateListModel(section, header, selectAll, mapList.get(list.get(i)));
                            }
                            break;
                    }
                } else {
                    addItemToModelList(section, header, selectAll, list.get(i), TYPE_ITEM);
                }
            }
        }

    }

    private HashMap<String, List<String>> getMapList(String section) {
        switch (section) {
            case HEADER_CT_SCANS:
                return getCtScanMapList();
            case HEADER_MRI:
                return getMriMapList();
            case HEADER_NUCLEAR_MEDICINE:
                return getNuclearMedicineMap();
            case HEADER_RADIOGRAPHY:
                return getRadiographyMapList();
            case HEADER_ULTRASONIC:
                return getUltrasonicMap();
        }
        return null;
    }

    private HashMap<String, Integer> getItemTypeMap(String section) {
        switch (section) {
            case HEADER_CT_SCANS:
                return getCtScanItemTypeMap();
            case HEADER_MR_ANGIOGRAPHY:
                return getMrAngiographItemType();
            case HEADER_CT_ANGIOGRAPH:
                return getCtAngiographItemType();
            case HEADER_MRI:
                return getMriItemTypeMap();
            case HEADER_NUCLEAR_MEDICINE:
                return getNuclearMedicineItemType();
            case HEADER_RADIOGRAPHY:
                return getRadiographyItemTypeMap();
            case HEADER_ULTRASONIC:
                return getUltrasonicItemType();
        }
        return null;
    }


    private void addItemToModelList(@Nullable String section, @Nullable String header, @Nullable String selectAll, @NonNull String item, @NonNull int type) {
        radiologyListModel = new RadiologyListModel();
        radiologyListModel.setType(type);
        radiologyListModel.setId(id);
        id++;
        switch (type) {
            case TYPE_SECTION:
                radiologyListModel.setSection(item);
                break;
            case TYPE_HEADER:
                radiologyListModel.setSection(section);
                radiologyListModel.setHeader(item);
                break;
            case TYPE_SUB_ITEM:
                radiologyListModel.setSection(section);
                radiologyListModel.setHeader(header);
                radiologyListModel.setSelectAll(selectAll);
                radiologyListModel.setItem(item);
                break;
            case TYPE_ITEM:
                radiologyListModel.setSection(section);
                radiologyListModel.setHeader(header);
                radiologyListModel.setItem(item);
                break;
            case TYPE_SELECT_ALL:
                radiologyListModel.setSection(section);
                radiologyListModel.setHeader(header);
                radiologyListModel.setItem(item);
                break;
            case TYPE_INPUT:
                radiologyListModel.setSection(section);
                radiologyListModel.setHeader(header);
                radiologyListModel.setItem(item);
                radiologyListModel.setIsAdditionalTextRequired(true);
                break;
            case TYPE_RL:
                radiologyListModel.setSection(section);
                radiologyListModel.setHeader(header);
                radiologyListModel.setItem(item);
                radiologyListModel.setIsRLType(true);
                break;
        }

        radiologyModelList.add(radiologyListModel);

    }

    /**
     * CT scans list start
     */

    private HashMap<String, Integer> getCtScanItemTypeMap() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("Other", TYPE_INPUT);
        typeMap.put("ABDOMEN & PELVIS", TYPE_HEADER);
        typeMap.put("CHEST/HEART", TYPE_HEADER);
        typeMap.put("HEAD/NECK", TYPE_HEADER);
        typeMap.put("EXTREMITY", TYPE_HEADER);
        typeMap.put("SPINE", TYPE_HEADER);
        typeMap.put("Chest with contrast**", TYPE_SELECT_ALL);
        typeMap.put("wlo contrast", TYPE_SUB_ITEM);
        typeMap.put("High Resolution Chest", TYPE_SELECT_ALL);
        typeMap.put("w/o contrast-Interstitial Disease", TYPE_SUB_ITEM);
        typeMap.put("Brain wlo", TYPE_SELECT_ALL);
        typeMap.put("w/o and w/ contrast**", TYPE_SUB_ITEM);
        typeMap.put("Orbits w/o", TYPE_SELECT_ALL);
        typeMap.put("w/contrast (for mass)**", TYPE_SUB_ITEM);
        typeMap.put("Shoulder R L", TYPE_RL);
        typeMap.put("Elbow R L", TYPE_RL);
        typeMap.put("Wrist R L", TYPE_RL);
        typeMap.put("Hip R L", TYPE_RL);
        typeMap.put("Knee R L", TYPE_RL);
        typeMap.put("Ankle R L", TYPE_RL);
        typeMap.put("Other R L", TYPE_RL);
        typeMap.put("Cervical", TYPE_SELECT_ALL);
        typeMap.put("Thoracic", TYPE_SELECT_ALL);
        typeMap.put("Lumbar", TYPE_SELECT_ALL);
        typeMap.put("Myelo", TYPE_SUB_ITEM);

        return typeMap;
    }


    public HashMap<String, List<String>> getCtScanMapList() {
        headerMap = new HashMap<>();

        headerMap.put("ABDOMEN & PELVIS", getCtScanAbdomenPelvisList());
        headerMap.put("CHEST/HEART", getCtScanChestHeartList());
        headerMap.put("HEAD/NECK", getCtScanHeadNeckList());
        headerMap.put("EXTREMITY", getCtScanExtremityList());
        headerMap.put("SPINE", getCtScanSpineList());
        headerMap.put("Chest with contrast**", getCtScanChestHeartCWCList());
        headerMap.put("High Resolution Chest", getCtScanChestHeartHRCList());
        headerMap.put("Brain wlo", getCtScanHeadNeckBrainList());
        headerMap.put("Orbits w/o", getCtScanHeadNeckOrbitsList());
        headerMap.put("Cervical", getCtScanSpineCervicalList());
        headerMap.put("Thoracic", getCtScanSpineThoracicList());
        headerMap.put("Lumbar", getCtScanSpineLumbarList());

        return headerMap;
    }

    private List<String> getCtScanList() {
        return new ArrayList<>(Arrays.asList("ABDOMEN & PELVIS", "CHEST/HEART", "HEAD/NECK", "EXTREMITY", "SPINE"));
    }

    private List<String> getCtScanAbdomenPelvisList() {
        return new ArrayList<>(Arrays.asList("Abdomen** ★", "Abdomen/Pelvis** ★", "Pelvis** ★",
                "Renal Stone Protocol", "CT - IVP / Urogram / Hematuria**", "Pancreas Protocol** ★",
                "Hemangioma Protocol**", "Other"));
    }

    private List<String> getCtScanChestHeartList() {
        return new ArrayList<>(Arrays.asList("Chest with contrast**", "High Resolution Chest", "Pulmonary Embolus Protocol**",
                "Coronary Artery Scoring (PDI Only)", "Other"));
    }

    private List<String> getCtScanChestHeartCWCList() {
        return new ArrayList<>(Arrays.asList("wlo contrast"));
    }

    private List<String> getCtScanChestHeartHRCList() {
        return new ArrayList<>(Arrays.asList("w/o contrast-Interstitial Disease"));
    }

    private List<String> getCtScanHeadNeckList() {
        return new ArrayList<>(Arrays.asList("Brain wlo", "Neck (soft tissue)**", "Orbits w/o", "Paranasal sinus (ltd)",
                "Paranasal Sinus (complete)", "Temporal Bones / IAC's", "Facial Bones", "Other"));
    }

    private List<String> getCtScanHeadNeckBrainList() {
        return new ArrayList<>(Arrays.asList("w/o and w/ contrast**"));
    }

    private List<String> getCtScanHeadNeckOrbitsList() {
        return new ArrayList<>(Arrays.asList("w/contrast (for mass)**"));
    }

    private List<String> getCtScanExtremityList() {
        return new ArrayList<>(Arrays.asList("Ankle R L", "Elbow R L", "Hip R L", "Knee R L", "Shoulder R L", "Wrist R L", "Other R L"));
    }

    private List<String> getCtScanSpineList() {
        return new ArrayList<>(Arrays.asList("Cervical", "Thoracic", "Lumbar"));
    }

    private List<String> getCtScanSpineCervicalList() {
        return new ArrayList<>(Arrays.asList("Myelo"));
    }

    private List<String> getCtScanSpineThoracicList() {
        return new ArrayList<>(Arrays.asList("Myelo"));
    }

    private List<String> getCtScanSpineLumbarList() {
        return new ArrayList<>(Arrays.asList("Myelo"));
    }


    /**
     * Radiography list start
     */

    private HashMap<String, Integer> getRadiographyItemTypeMap() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("ABDOMEN / PELVIS", TYPE_HEADER);
        typeMap.put("CHEST / UPPER BODY", TYPE_HEADER);
        typeMap.put("FLUOROSCOPY (MMC Only)", TYPE_HEADER);
        typeMap.put("HEAD / NECK", TYPE_HEADER);
        typeMap.put("LOWER EXTREMITY", TYPE_HEADER);
        typeMap.put("SPINE", TYPE_HEADER);
        typeMap.put("Hip R L", TYPE_RL);
        typeMap.put("Clavicle R L", TYPE_RL);
        typeMap.put("Elbow R L", TYPE_RL);
        typeMap.put("Finger R L", TYPE_RL);
        typeMap.put("Forearm R L", TYPE_RL);
        typeMap.put("Hand R L", TYPE_RL);
        typeMap.put("Humerus R L", TYPE_RL);
        typeMap.put("Ribs Unilateral R L", TYPE_RL);
        typeMap.put("Scapula R L", TYPE_RL);
        typeMap.put("Shoulder RL", TYPE_RL);
        typeMap.put("Wrist R L", TYPE_RL);
        typeMap.put("Wrist with Navicular R L", TYPE_RL);
        typeMap.put("Ankle R L", TYPE_RL);
        typeMap.put("Femur R L", TYPE_RL);
        typeMap.put("Heel R L", TYPE_RL);
        typeMap.put("Knee R L", TYPE_RL);
        typeMap.put("Tibia/Fibula R L", TYPE_RL);
        typeMap.put("Foot R L", TYPE_RL);
        typeMap.put("Toes R L", TYPE_RL);

        return typeMap;
    }

    private HashMap<String, List<String>> getRadiographyMapList() {
        headerMap = new HashMap<>();
        headerMap.put("ABDOMEN / PELVIS", getRadiographyAbdomenPelvisList());
        headerMap.put("CHEST / UPPER BODY", getRadiographyChestUpperBodyList());
        headerMap.put("FLUOROSCOPY (MMC Only)", getRadiographyFluoroscopyList());
        headerMap.put("HEAD / NECK", getRadiographyHeadNeckList());
        headerMap.put("LOWER EXTREMITY", getRadiographyLowerExtremityList());
        headerMap.put("SPINE", getRadiographySpineList());

        return headerMap;
    }

    private List<String> getRadiographyList() {
        return new ArrayList<>(Arrays.asList("ABDOMEN / PELVIS", "CHEST / UPPER BODY", "FLUOROSCOPY (MMC Only)",
                "HEAD / NECK", "LOWER EXTREMITY", "SPINE"));
    }

    private List<String> getRadiographyAbdomenPelvisList() {
        return new ArrayList<>(Arrays.asList("KUB 1 View", "Abdomen 2 View", "Acute Abdominal Series", "Hip R L",
                "Pelvis", "Sacrum & Coccyx", "SI Joints"));
    }

    private List<String> getRadiographyChestUpperBodyList() {
        return new ArrayList<>(Arrays.asList("A/C Joints (bilateral)", "Chest 1 View", "Chest 2 View", "Clavicle R L", "Elbow R L",
                "Finger R L", "Forearm R L", "Hand R L", "Humerus R L", "Ribs Bilateral", "Ribs Unilateral R L",
                "Scapula R L", "Shoulder RL", "Sternum", "Wrist R L", "Wrist with Navicular R L", "Bone Age (left hand/wrist)"));
    }

    private List<String> getRadiographyFluoroscopyList() {
        return new ArrayList<>(Arrays.asList("Upper GI Series Ø", "Esophagram", "Modified Barium Swallow w/ Speech Therapist",
                "Enema/Lower GI Ø✪", "Small Bowel Series Ø", "IVP** Ø✪", "VCUG", "Hysterosalpingogram", "Lumbar Puncture"));
    }

    private List<String> getRadiographyHeadNeckList() {
        return new ArrayList<>(Arrays.asList("Facial Bones", "Mandible", "Nasal Bones", "Sinuses", "Orbits (limited/screening)", "Skull", "Temporomandibular Joints"));
    }

    private List<String> getRadiographyLowerExtremityList() {
        return new ArrayList<>(Arrays.asList("Ankle R L", "Femur R L", "Heel R L", "Knee R L", "Tibia/Fibula R L",
                "Foot R L", "Toes R L", "Bilateral Hip to Toes Pediatric < One Year"));
    }

    private List<String> getRadiographySpineList() {
        return new ArrayList<>(Arrays.asList("Cervical Spine", "Thoracic Spine", "Thoraco-Lumbar", "Lumbar Spine", "Scoliosis Study"));
    }

    /**
     * MRI list start
     */

    private HashMap<String, Integer> getMriItemTypeMap() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("ABDOMEN / PELVIS", TYPE_HEADER);
        typeMap.put("CHEST / HEART", TYPE_HEADER);
        typeMap.put("EXTREMITY", TYPE_HEADER);
        typeMap.put("Extremities Only", TYPE_HEADER);
        typeMap.put("HEAD / NECK", TYPE_HEADER);
        typeMap.put("SPINE", TYPE_HEADER);
        typeMap.put("Knee R L", TYPE_RL);
        typeMap.put("Shoulder RL", TYPE_RL);
        typeMap.put("Foot R L", TYPE_RL);
        typeMap.put("Ankle R L", TYPE_RL);
        typeMap.put("Wrist R L", TYPE_RL);
        typeMap.put("Elbow R L", TYPE_RL);
        typeMap.put("Hip R L", TYPE_RL);
        typeMap.put("Brain w/o & with", TYPE_SELECT_ALL);
        typeMap.put("IAC's", TYPE_SUB_ITEM);
        typeMap.put("Pituitary", TYPE_SUB_ITEM);
        typeMap.put("Seizure", TYPE_SUB_ITEM);
        typeMap.put("OMS Protocol", TYPE_SUB_ITEM);

        return typeMap;
    }

    private HashMap<String, List<String>> getMriMapList() {
        headerMap = new HashMap<>();
        headerMap.put("ABDOMEN / PELVIS", getMriAbdomenPelvisList());
        headerMap.put("CHEST / HEART", getMriChestHeartList());
        headerMap.put("EXTREMITY", getMriExtrimityList());
        headerMap.put("Extremities Only", getMriExtrimitiesOnlyList());
        headerMap.put("HEAD / NECK", getMriHeadNeckList());
        headerMap.put("SPINE", getMriSpineList());

        return headerMap;
    }

    private List<String> getMriList() {
        return new ArrayList<>(Arrays.asList("ABDOMEN / PELVIS", "CHEST / HEART", "EXTREMITY", "Extremities Only",
                "HEAD / NECK", "SPINE"));
    }

    private List<String> getMriAbdomenPelvisList() {
        return new ArrayList<>(Arrays.asList("MRCP Ø", "Liver w/o & with Ø", "Adrenal / Kidney Ø", "Pelvis", "Neuro Pelvis"));
    }

    private List<String> getMriChestHeartList() {
        return new ArrayList<>(Arrays.asList("Bilateral Breast w/o & with (MMC only)", "Chest", "Brachial Plexus", "Cardiac w/o & with (PDI Only)"));
    }

    private List<String> getMriExtrimityList() {
        return new ArrayList<>(Arrays.asList("Ankle R L", "Elbow R L", "Foot R L", "Hip R L", "Knee R L", "Shoulder R L", "Wrist R L"));
    }

    private List<String> getMriExtrimitiesOnlyList() {
        return new ArrayList<>(Arrays.asList("No Contrast", "W / IV Contrast", "Intra-articular Contrast", "IA Contrast & Pain Management"));
    }

    private List<String> getMriHeadNeckList() {
        return new ArrayList<>(Arrays.asList("Brain", "Brain w/o & with", "Orbits w/o & with", "TMJ's (PDI Only)",
                "Facial w/o & with", "Soft Tissue Neck w/o & with"));
    }

    private List<String> getMriBrainWOList() {
        return new ArrayList<>(Arrays.asList("IAC's", "Pituitary", "Seizure", "OMS Protocol"));
    }

    private List<String> getMriSpineList() {
        return new ArrayList<>(Arrays.asList("Cervical Spine", "Thoracic Spine", "Lumbar Spine", "Sacrum"));
    }

    /**
     * Nuclear medicine list start
     */

    private HashMap<String, Integer> getNuclearMedicineItemType() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("Bone Scan", TYPE_SELECT_ALL);
        typeMap.put("Thyroid *❄", TYPE_SELECT_ALL);
        typeMap.put("Whole Body", TYPE_SUB_ITEM);
        typeMap.put("Limited", TYPE_SUB_ITEM);
        typeMap.put("Other", TYPE_INPUT);
        typeMap.put("Triple Phase(Osteo & Extremities) Area", TYPE_INPUT);
        typeMap.put("l - 123 Uptake and Scan", TYPE_SUB_ITEM);
        typeMap.put("l - 131 Thyroid Therapy (Treatment for Hyperthyroidism, Graves)", TYPE_SUB_ITEM);
        typeMap.put("I - 131 Thyroid Ablation (Thyroid CA)", TYPE_SUB_ITEM);
        typeMap.put("l - 131 Whole Body for Mets", TYPE_SUB_ITEM);
        return typeMap;
    }

    private HashMap<String, List<String>> getNuclearMedicineMap() {
        headerMap = new HashMap<>();
        headerMap.put("Bone Scan", getBoneScanList());
        headerMap.put("Thyroid *❄", getBoneScanList());

        return headerMap;
    }

    private List<String> getNuclearMedicineList() {
        return new ArrayList<>(Arrays.asList("Bone Scan", "Cisternogram", "Gallium", "Hida Ø", "Lung Scan",
                "Melanoma Only Ø", "Other", "Parathyroid", "PET/Bone F-18", "PET/CT Skull to Thigh Ø", "PET/CT Whole Body",
                "Renogram \uD83D\uDCA7", "Thyroid *❄"));
    }

    private List<String> getThyroidList() {
        return new ArrayList<>(Arrays.asList("l - 123 Uptake and Scan", "l - 131 Thyroid Therapy (Treatment for Hyperthyroidism, Graves)",
                "I - 131 Thyroid Ablation (Thyroid CA)", "l - 131 Whole Body for Mets"));
    }

    private List<String> getBoneScanList() {
        return new ArrayList<>(Arrays.asList("Whole Body", "Limited", "Triple Phase(Osteo & Extremities) Area"));
    }

    /**
     * Ultrasound list start
     */

    private HashMap<String, Integer> getUltrasonicItemType() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("INFANT", TYPE_HEADER);
        typeMap.put("Abdomen Complete Ø", TYPE_SELECT_ALL);
        typeMap.put("Abdomen Limited Ø", TYPE_SUB_ITEM);
        typeMap.put("Gallbladder / RUQ Ø", TYPE_SUB_ITEM);
        typeMap.put("Liver Ø", TYPE_SUB_ITEM);
        typeMap.put("Kidneys / Renal Ø", TYPE_SUB_ITEM);
        typeMap.put("Pancreas Ø", TYPE_SUB_ITEM);
        typeMap.put("Spleen Ø", TYPE_SUB_ITEM);
        typeMap.put("Aspiration", TYPE_INPUT);
        typeMap.put("Biopsy", TYPE_INPUT);
        typeMap.put("Soft Tissue", TYPE_INPUT);
        typeMap.put("Other", TYPE_INPUT);
        return typeMap;
    }

    private HashMap<String, List<String>> getUltrasonicMap() {
        headerMap = new HashMap<>();
        headerMap.put("INFANT", getUltrasoundInfantList());
        headerMap.put("Abdomen Complete Ø", getAbdomenCompleteList());

        return headerMap;
    }

    private List<String> getUltrasoundList() {
        return new ArrayList<>(Arrays.asList("Abdomen Complete Ø", "Aspiration", "Biopsy", "Bladder  \uD83D\uDCA7",
                "Breast R L.", "Inguinal Hernia", "OB Complete", "OB Limited", "Paracentesis", "Thoracentesis",
                "Pelvis with Endovaginal", "Endovaginal Only", "Pelvis Only", "Renal Artery Stenosis (MMC Only)",
                "Renal Vascular", "Soft Tissue", "Thyroid", "Thyroid Biopsy", "Extremity-Non Vascular R L", "Other", "INFANT"));
    }

    private List<String> getAbdomenCompleteList() {
        return new ArrayList<>(Arrays.asList("Abdomen Limited Ø", "Gallbladder / RUQ Ø", "Liver Ø",
                "Kidneys / Renal Ø", "Pancreas Ø", "Spleen Ø", "Aorta Ø"));
    }

    private List<String> getUltrasoundInfantList() {
        return new ArrayList<>(Arrays.asList("Brain", "Hips", "Spine", "Pylorus"));
    }

    /**
     * CT Angiography list start
     */
    private HashMap<String, Integer> getCtAngiographItemType() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("Other", TYPE_INPUT);
        typeMap.put("Upper Extremity Arteries R L", TYPE_RL);
        return typeMap;
    }

    private List<String> getCtAngiographyList() {
        return new ArrayList<>(Arrays.asList("Abdominal Aorta",
                "Abdominal Aorta & Runoff", "Cervical Carotids", "Circle of Willis", "Mesenteric Arteries",
                "Pulmonary Arteries", "Renal Arteries", "Thoracic Aorta", "Upper Extremity Arteries R L", "Other"));
    }


    /**
     * MR Angiography list start
     */

    private HashMap<String, Integer> getMrAngiographItemType() {
        HashMap<String, Integer> typeMap = new HashMap<>();
        typeMap.put("Other", TYPE_INPUT);
        typeMap.put("Venous ", TYPE_INPUT);
        typeMap.put("Upper Extremity** R L", TYPE_RL);
        return typeMap;
    }

    private List<String> getMrAngiographyList() {
        return new ArrayList<>(Arrays.asList("Abdominal Aorta**", "Abdominal Aorta** & Runoff",
                "Carotids / Vertebrals", "Cirle of Willis / Brain", "Mesenteric Arteries**", "Portal Vein",
                "Renal Arteries**", "Thoracic Aorta**", "Upper Extremity** R L", "Venous (specify)", "Other"));
    }

}
