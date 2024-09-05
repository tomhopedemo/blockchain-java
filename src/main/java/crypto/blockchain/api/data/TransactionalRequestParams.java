package crypto.blockchain.api.data;

public record TransactionalRequestParams(String from, String currency, String to, Long value, String type) {

    public static class TransactionalRequestParamsBuilder {
        private String from;
        private String currency;
        private String string;
        private Long value;
        private String type;

        public TransactionalRequestParamsBuilder setFrom(String from) {
            this.from = from;
            return this;
        }

        public TransactionalRequestParamsBuilder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionalRequestParamsBuilder setTo(String string) {
            this.string = string;
            return this;
        }

        public TransactionalRequestParamsBuilder setValue(Long value) {
            this.value = value;
            return this;
        }

        public TransactionalRequestParamsBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public TransactionalRequestParams build() {
            return new TransactionalRequestParams(from, currency, string, value, type);
        }
    }

}

