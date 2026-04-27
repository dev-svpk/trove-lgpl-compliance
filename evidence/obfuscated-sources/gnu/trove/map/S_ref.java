package gnu.trove.map;

public interface S_ref {
   int getNoEntryValue();

   int put(long var1, int var3);

   int get(long var1);

   int size();

   gnu.trove.c_ref.Z_ref iterator();
}
