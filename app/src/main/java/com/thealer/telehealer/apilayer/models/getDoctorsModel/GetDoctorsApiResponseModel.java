package com.thealer.telehealer.apilayer.models.getDoctorsModel;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.EducationsBean;
import com.thealer.telehealer.apilayer.models.createuser.OtherInformation;
import com.thealer.telehealer.apilayer.models.createuser.PhonesBean;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.apilayer.models.createuser.SpecialtiesBean;
import com.thealer.telehealer.apilayer.models.createuser.VisitAddressBean;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 25,October,2018
 */
public class GetDoctorsApiResponseModel extends BaseApiResponseModel implements Serializable {

    private int total_count;
    private int result_count;
    private int page_size;
    private int current_page;
    private int prev_page;
    private int next_page;
    private List<DataBean> data;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getResult_count() {
        return result_count;
    }

    public void setResult_count(int result_count) {
        this.result_count = result_count;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getPrev_page() {
        return prev_page;
    }

    public void setPrev_page(int prev_page) {
        this.prev_page = prev_page;
    }

    public int getNext_page() {
        return next_page;
    }

    public void setNext_page(int next_page) {
        this.next_page = next_page;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {

        private String uid;
        private String npi;
        private String liability;
        private ProfileBean profile;
        private OtherInformation otherInformation;
        private List<SpecialtiesBean> specialties;
        private List<LicensesBean> licenses;
        private List<EducationsBean> educations;
        private List<PracticesBean> practices;

        public String getDoctorDisplayName() {
            String title = null;
            if (getProfile().getTitle() != null && !getProfile().getTitle().isEmpty()) {
                title = ", " + getProfile().getTitle();
            }
            return Utils.getDoctorDisplayName(getProfile().getFirst_name(), getProfile().getLast_name(), title);
        }


        public String getDoctorSpecialist() {
            if (getSpecialties() != null
                    && getSpecialties().size() > 0) {
                return getSpecialties().get(0).getName();
            }
            return "";
        }

        public String getDoctorAddress() {

            if (getPractices().size() > 0 &&
                    getPractices().get(0).getVisit_address() != null) {
                VisitAddressBean visitAddressBean = getPractices().get(0).getVisit_address();

                return visitAddressBean.getStreet() + "," + visitAddressBean.getCity() + "," + visitAddressBean.getState() + "," + visitAddressBean.getZip();

            } else {
                return "";
            }
        }

        public String getDoctorPhone() {
            if (getPractices() != null &&
                    getPractices().size() > 0 &&
                    getPractices().get(0).getPhones() != null &&
                    getPractices().get(0).getPhones().size() > 0) {

                return getPractices().get(0).getPhones().get(0).getNumber();

            } else {
                return "";
            }
        }

        public OtherInformation getOtherInformation() {
            return otherInformation;
        }

        public void setOtherInformation(OtherInformation otherInformation) {
            this.otherInformation = otherInformation;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getNpi() {
            return npi;
        }

        public void setNpi(String npi) {
            this.npi = npi;
        }

        public String getLiability() {
            return liability;
        }

        public void setLiability(String liability) {
            this.liability = liability;
        }

        public ProfileBean getProfile() {
            return profile;
        }

        public void setProfile(ProfileBean profile) {
            this.profile = profile;
        }

        public List<SpecialtiesBean> getSpecialties() {
            return specialties;
        }

        public void setSpecialties(List<SpecialtiesBean> specialties) {
            this.specialties = specialties;
        }

        public List<LicensesBean> getLicenses() {
            return licenses;
        }

        public void setLicenses(List<LicensesBean> licenses) {
            this.licenses = licenses;
        }

        public List<EducationsBean> getEducations() {
            return educations;
        }

        public void setEducations(List<EducationsBean> educations) {
            this.educations = educations;
        }

        public List<PracticesBean> getPractices() {
            return practices;
        }

        public void setPractices(List<PracticesBean> practices) {
            this.practices = practices;
        }

        public static class ProfileBean implements Serializable {

            private String gender;
            private String image_url;
            private String last_name;
            private String bio;
            private String middle_name;
            private String title;
            private String first_name;
            private String slug;
            private List<LanguagesBean> languages;

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getLast_name() {
                return last_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getBio() {
                return bio;
            }

            public void setBio(String bio) {
                this.bio = bio;
            }

            public String getMiddle_name() {
                return middle_name;
            }

            public void setMiddle_name(String middle_name) {
                this.middle_name = middle_name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getSlug() {
                return slug;
            }

            public void setSlug(String slug) {
                this.slug = slug;
            }

            public List<LanguagesBean> getLanguages() {
                return languages;
            }

            public void setLanguages(List<LanguagesBean> languages) {
                this.languages = languages;
            }

            public static class LanguagesBean implements Serializable {

                private String code;
                private String name;

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
            }
        }

        public static class LicensesBean implements Serializable {

            private String state;
            private String number;
            private String end_date;

            public LicensesBean() {
            }

            public LicensesBean(String state, String number, String end_date) {
                this.state = state;
                this.number = number;
                this.end_date = end_date;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getEnd_date() {
                return end_date;
            }

            public void setEnd_date(String end_date) {
                this.end_date = end_date;
            }

            public boolean isEqual(com.thealer.telehealer.apilayer.models.createuser.LicensesBean licensesBean) {
                return licensesBean.getState().equals(getState()) && licensesBean.getNumber().equals(getNumber()) && Utils.getDateFromPossibleFormat(getEnd_date()).equals(Utils.getDateFromPossibleFormat(licensesBean.getEnd_date()));
            }
        }

        public static class SpecialtiesBean implements Serializable {

            private String actors;
            private String description;
            private String category;
            private String uid;
            private String name;
            private String actor;

            public SpecialtiesBean() {
            }

            public SpecialtiesBean(String actors, String description, String category, String uid, String name, String actor) {
                this.actors = actors;
                this.description = description;
                this.category = category;
                this.uid = uid;
                this.name = name;
                this.actor = actor;
            }

            public String getActors() {
                return actors;
            }

            public void setActors(String actors) {
                this.actors = actors;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getActor() {
                return actor;
            }

            public void setActor(String actor) {
                this.actor = actor;
            }
        }

        public static class PracticesBean implements Serializable{

            private String name;
            private String website;
            private VisitAddressBean visit_address;
            private List<PhonesBean> phones = new ArrayList<>();

            public PracticesBean() {
            }

            public PracticesBean(String name, String website, VisitAddressBean visit_address, List<PhonesBean> phones) {
                this.name = name;
                this.website = website;
                this.visit_address = visit_address;
                this.phones = phones;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getWebsite() {
                return website;
            }

            public void setWebsite(String website) {
                this.website = website;
            }

            public VisitAddressBean getVisit_address() {
                return visit_address;
            }

            public void setVisit_address(VisitAddressBean visit_address) {
                this.visit_address = visit_address;
            }

            public List<PhonesBean> getPhones() {
                return phones;
            }

            public void setPhones(List<PhonesBean> phones) {
                this.phones = phones;
            }

        }

    }
}
