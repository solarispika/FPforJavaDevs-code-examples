package datastructures3;
import datastructures3.ListModule.List;
import static datastructures3.ListModule.*;
import functions.Function1Void;

public class ForeachExample {
  public static void main(String[] args) {
    argsToList(args).foreach(new Function1Void<String>() {
      public void apply(String arg) {
        System.out.println("You entered: "+arg);
      }
    });
  }

  private static List<String> argsToList(String[] args) {
    List<String> result = emptyList();
    for (String arg: args) {
      result = list(arg, result);
    }
    return reverse(result);
  }
}
