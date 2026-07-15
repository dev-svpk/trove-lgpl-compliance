package gnu.trove.map;

public interface TCharObjectMap {
   char getNoEntryKey();

   int size();

   boolean containsKey(char var1);

   Object get(char var1);

   boolean forEachEntry(gnu.trove.procedure.TCharObjectProcedure var1);
}
