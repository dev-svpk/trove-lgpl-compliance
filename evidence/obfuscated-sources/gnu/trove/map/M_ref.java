package gnu.trove.map;

public interface M_ref {
   int getNoEntryKey();

   int size();

   boolean containsKey(int var1);

   Object get(int var1);

   boolean forEachEntry(gnu.trove.e_ref.Q_ref var1);
}
