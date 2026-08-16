public class Main {
    public static void main(String[] args) {
        Appliance fridge = new WhiteGoods("A001", "Fridge X1", "Samsung", 1500.00, 10, "5-star", "180x60x65cm");
        Appliance tv = new DigitalGadgets("A002", "Smart TV Y2", "Sony", 2000.00, 2, "Android TV", 150.0);

        System.out.println(fridge.getModelName() + " final price: " + fridge.calculateFinalPrice());
        System.out.println(tv.getModelName() + " final price: " + tv.calculateFinalPrice());
        System.out.println(tv.getModelName() + " low stock? " + tv.isLowStock());
    }
}