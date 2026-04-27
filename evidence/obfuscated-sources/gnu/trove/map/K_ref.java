package gnu.trove.map;

public interface K_ref {
   int getNoEntryValue();

   int put(int var1, int var2);

   int get(int var1);

   int size();

   gnu.trove.c_ref.P_ref iterator();
}
