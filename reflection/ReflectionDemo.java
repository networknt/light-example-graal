public class ReflectionDemo {

   public static void main(String[] args) {

      try {
         // returns the Class object for the class with the specified name
         Class cls = Class.forName(args[0]);
         // returns the name and package of the class
         System.out.println("Class found = " + cls.getName());
         System.out.println("Package = " + cls.getPackage());

         // create an instance of the cls. 
         // This requires that the class has a default constructor
         Object obj = cls.getConstructor().newInstance();
         System.out.println("Object created = " + obj);
      } catch(Exception ex) {
         System.out.println(ex.toString());
      }
   }
}

