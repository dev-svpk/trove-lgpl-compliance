package gnu.trove.map;

public interface Q_ref {
   double getNoEntryValue();

   double put(long var1, double var3);

   double get(long var1);

   int size();

   boolean containsKey(long var1);

   gnu.trove.c_ref.X_ref iterator();
}
