public class VRPTWSolution {
/*
Problem instance: RC104. Distance: 1135.48. Vehicles: 10.
route 1: 86 64 90 77 85 52 65 84 67;
route 2: 23 21 50 20 24 22 49 19 26 25;
route 3: 81 92 57 96 63 68 95 94 72 73 55 97 82;
route 4: 62 71 2 4 6 46 9 47 5 101 69;
route 5: 91 66 100 98 76 59 78;
route 6: 43 45 44 39 38 36 37 41 40 42;
route 7: 89 61 79 74 80 8 7 3 56;
route 8: 70 99 54 13 18 17 14 11 83;
route 9: 93 51 34 33 31 29 27 28 30 32 35;
route 10: 15 48 16 12 10 88 60 75 87 58 53;
*/

  String instanceName;
  double distance;
  int _vehicles;
  boolean completed;
  LinkedList<Integer>[] routes;

  public VRPTWSolution(String name, int vehicles) {
    instanceName = name;
    completed = false;
    _vehicles = vehicles;
    routes = new LinkedList<Integer>[vehicles];
  }

  public show() {
    if (completed) {
      System.out.println("Soluzione completa");
    } else {
      System.out.println("Soluzione parziale");
    }
  }

}