package bgu.spl181.net.srv.bidi;
//package com.example;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

    public class Movie {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("bannedCountries")
        @Expose
        private List<String> bannedCountries = null;
        @SerializedName("availableAmount")
        @Expose
        private int availableAmount;
        @SerializedName("totalAmount")
        @Expose
        private int totalAmount;

        /**
         * No args constructor for use in serialization
         *
         */
        public Movie() {
        }

        /**
         *
         * @param availableAmount
         * @param id
         * @param price
         * @param name
         * @param totalAmount
         * @param bannedCountries
         */
        public Movie(String id, String name, String price, List<String> bannedCountries, int availableAmount, int totalAmount) {
            super();
            this.id = id;
            this.name = name;
            this.price = price;
            this.bannedCountries = bannedCountries;
            this.availableAmount = availableAmount;
            this.totalAmount = totalAmount;
        }
        public Movie(String id, String name) {
            super();
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public List<String> getBannedCountries() {
            return bannedCountries;
        }

        public void setBannedCountries(List<String> bannedCountries) {
            this.bannedCountries = bannedCountries;
        }

        public int getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(int availableAmount) {
            this.availableAmount = availableAmount;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(int totalAmount) {
            this.totalAmount = totalAmount;
        }


    }



