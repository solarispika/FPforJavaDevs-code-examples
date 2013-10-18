package datastructures3;
import java.util.Arrays;
import java.util.Stack;
import functions.*;
import option.*;

public class ListModule {
  public static interface List<T> {

    public abstract Option<T>           head();
    public abstract Option<List<T>>     tail();
    public abstract boolean             isEmpty();

    public      List<T>  filter    (Function1<T,Boolean> f);
    public <T2> List<T2> map       (Function1<T,T2> f);
    public <T2> T2       foldLeft  (T2 seed, Function2<T2,T,T2> f);
    public <T2> T2       foldRight (T2 seed, Function2<T,T2,T2> f);
    public      void     foreach   (Function1Void<T> f);
  }

  public static final class NonEmptyList<T> implements List<T> {

    public Option<T>       head()    { return new Some<T>(_head); }
    public Option<List<T>> tail()    { return new Some<List<T>>(_tail); }
    public boolean         isEmpty() { return false; }

    protected NonEmptyList(T head, List<T> tail) {
      this._head = head;
      this._tail = tail;
    }

    private final T _head;
    private final List<T> _tail;

    public List<T> filter (Function1<T,Boolean> f) {
      return foldRight(emptyList(), new Function2<T,List<T>,List<T>>() {
        public List<T> apply(T elem, List<T> acc) {
          if (f.apply(elem))
            return list(elem, acc);
          else
            return acc;
        }
      });
    }

    public <T2> List<T2> map (Function1<T,T2> f) {
      return foldRight(emptyList(), new Function2<T,List<T2>,List<T2>>() {
        public List<T2> apply(T elem, List<T2> acc) {
          return list(f.apply(elem), acc);
        }
      });
    }

    public <T2> T2 foldLeft (T2 seed, Function2<T2,T,T2> f) {
      List<T> tmp = this;
      T2 tmp_seed = seed;
      while (!tmp.equals(emptyList())) {
        tmp_seed = f.apply(tmp_seed, tmp.head().get());
        tmp = tmp.tail().get();
      }
      return tmp_seed;
    }

    public <T2> T2 foldRight (T2 seed, Function2<T,T2,T2> f) {
      List<T> tmp = this;
      Stack<T> stack = new Stack<T>();
      T2 tmp_seed = seed;

      while (!tmp.equals(emptyList())) {
        stack.push(tmp.head().get());
        tmp = tmp.tail().get();
      }
      while (!stack.empty()) {
        T elem = stack.pop();
        tmp_seed = f.apply(elem, tmp_seed);
      }
      return tmp_seed;
    }

    public void foreach (Function1Void<T> f) {
      f.apply(head().get());                                         // #11
      tail().get().foreach(f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
      if (other == null || getClass() != other.getClass())
        return false;
      List<T> that = (List<T>) other;
      Option<List<T>> rr = foldLeft(new Some<List<T>>(that),
          new Function2<Option<List<T>>,T,Option<List<T>>>() {
          public Option<List<T>> apply(Option<List<T>> rem, T elem) {
            if (!rem.hasValue() ||
                !rem.get().head().hasValue() ||
                !rem.get().head().get().equals(elem))
              return new None<List<T>>();
            else
              return rem.get().tail();
          }
      });
      return rr.hasValue() && rr.get().isEmpty();
    }

    @Override
    public int hashCode() { return 37*(head().get().hashCode()+tail().get().hashCode()); }

    @Override
    public String toString() {
      return foldRight("()", new Function2<T, String, String>() {
          public String apply(T elem, String acc) {
            acc = "(" + elem + ", " + acc + ")";
            return acc;
          }
        });
    }
  }

  public static final List<? extends Object> EMPTY = new List<Object>() {

    public Option<Object>       head()    { return new None<Object>(); }
    public Option<List<Object>> tail()    { return new None<List<Object>>(); }
    public boolean              isEmpty() { return true; }

    public      List<Object> filter (Function1<Object,Boolean> f) { return this; }
    public <T2> List<T2>  map (Function1<Object,T2> f) { return emptyList(); }

    public <T2> T2 foldLeft  (T2 seed, Function2<T2,Object,T2> f) { return seed; }
    public <T2> T2 foldRight (T2 seed, Function2<Object,T2,T2> f) { return seed; }

    public void foreach (Function1Void<Object> f) {}

    @Override
    public String toString() { return "()"; }
  };

  /* See the text for an explanation of this code */
  @SuppressWarnings(value = "unchecked")
  public static <T> List<T> emptyList() {
    return (List<T>) EMPTY; // Dangerous!?
  }

  public static <T> List<T> list(T head, List<T> tail) {
    return new NonEmptyList<T>(head, tail);
  }

  @SafeVarargs
  public static <T> List<T> list(T ... elems) {
    if (elems.length == 0)
      return emptyList();
    return new NonEmptyList<T>(elems[0], list(Arrays.copyOfRange(elems, 1, elems.length)));
  }

  public static <T> List<T> reverse(List<T> list) {
    return list.foldLeft(emptyList(), new Function2<List<T>,T,List<T>>() {
      public List<T> apply(List<T> acc, T elem) {
        return list(elem, acc);
      }
    });
  }
}
