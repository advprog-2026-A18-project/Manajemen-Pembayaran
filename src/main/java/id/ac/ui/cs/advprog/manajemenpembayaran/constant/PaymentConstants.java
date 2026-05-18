package id.ac.ui.cs.advprog.manajemenpembayaran.constant;

public final class PaymentConstants {

    private PaymentConstants() {
    }

    public static final class Owner {
        public static final String ADMIN_DEFAULT = "admin-default";
        public static final String MANDOR_DEFAULT = "mandor-default";
        public static final String BURUH_DEFAULT = "buruh-default";
        public static final String SUPIR_DEFAULT = "supir-default";

        private Owner() {
        }
    }

    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String MANDOR = "MANDOR";
        public static final String BURUH = "BURUH";
        public static final String SUPIR = "SUPIR";

        private Role() {
        }
    }

    public static final class Reference {
        public static final String TOP_UP_REQUEST = "TOP_UP_REQUEST";
        public static final String PAYROLL = "PAYROLL";

        private Reference() {
        }
    }
}
