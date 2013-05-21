public class APITester {

    private static ClassLoader classLoader = APITester.class.getClassLoader();

    public static void testStage1() throws ClassNotFoundException {
        testClasses(new String[]{"geometry2d.Point", "geometry2d.Rectangle",
                                 "geometry2d.Transformation", "geometry2d.AffineTransformation"});
    }

    public static void testStage2() throws ClassNotFoundException {
        // no mandatory classes
    }

    public static void testStage3() throws ClassNotFoundException {
        testClasses(new String[]{"flame.Flame", "flame.FlameTransformation",
                                 "flame.FlameAccumulator", "flame.Variation"});
    }

    public static void testStage4() throws ClassNotFoundException {
        testClasses(new String[]{"color.Color", "color.Palette", "color.InterpolatedPalette",
                                 "color.RandomPalette", "flame.FlamePPMMaker"});
    }

    private static void testClasses(String[] classNames) throws ClassNotFoundException {
        for(String clazz: classNames) {
            try {
                classLoader.loadClass("ch.epfl.flamemaker." + clazz);
            } catch (ClassNotFoundException e) {
                System.err.println("Could not find the following class: ch.epfl.flamemaker." + 
                                   clazz);
                throw e;
            }
        }
    }


    public static void main(String[] args) {
        System.out.println("Welcome to the API tester for the PTI project.");
        System.out.println("For any issue with the tool, please contact Michele Catasta " +
                           "(michele.catasta@epfl.ch).\n");

        // Replicating JUnit functionalities just to avoid the classpath dependency...
        try {
            testStage1();
            testStage2();
            testStage3();
            testStage4();
            System.out.println("All tests passed!");
        } catch (ClassNotFoundException e) {
            System.err.println("The API tester failed. If you still want to submit your project " +
                               "with errors, use the \"jar-emergency\" Ant target.");
            System.exit(42);
        }
    }
}
