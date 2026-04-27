package gnu.trove.map;

public interface I_ref {
   double getNoEntryValue();

   double put(int var1, double var2);

   double get(int var1);

   void clear();

   int size();

   boolean containsKey(int var1);

   gnu.trove.c_ref.N_ref iterator();
}
