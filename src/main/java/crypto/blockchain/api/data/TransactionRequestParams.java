package crypto.blockchain.api.data;

public record TransactionRequestParams(String from, String currency, String to, Long value) {

    public static class Builder {
        private String from;
        private String currency;
        private String string;
        private Long value;

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setTo(String string) {
            this.string = string;
            return this;
        }

        public Builder setValue(Long value) {
            this.value = value;
            return this;
        }

        public TransactionRequestParams build() {
            return new TransactionRequestParams(from, currency, string, value);
        }
    }

}

