package gnu.trove.map;

public interface TIntObjectMap {
   int getNoEntryKey();

   int size();

   boolean containsKey(int var1);

   Object get(int var1);

   boolean forEachEntry(gnu.trove.procedure.TIntObjectProcedure var1);
}
