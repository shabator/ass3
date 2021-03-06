package bgu.spl181.net.srv.bidi;


import java.util.List;

    public class Movie {


        private String id;

        private String name;

        private String price;

        private String [] bannedCountries = null;

        private String availableAmount;

        private String totalAmount;

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
        public Movie(String id, String name, String price, String [] bannedCountries, String availableAmount, String totalAmount) {
            super();
            this.id = id;
            this.name = name;
            this.price = price;
            this.bannedCountries = bannedCountries;
            this.availableAmount = availableAmount;
            this.totalAmount = totalAmount;

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

        public String[] getBannedCountries() {
            return bannedCountries;
        }

        public void setBannedCountries(String[] bannedCountries) {
            this.bannedCountries = bannedCountries;
        }

        public String getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(String availableAmount) {
            this.availableAmount = availableAmount;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }


    }



