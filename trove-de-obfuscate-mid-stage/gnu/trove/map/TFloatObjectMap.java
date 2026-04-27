package gnu.trove.map;

public interface TFloatObjectMap {
   float getNoEntryKey();

   int size();

   boolean containsKey(float var1);

   Object get(float var1);

   boolean forEachEntry(gnu.trove.procedure.TFloatObjectProcedure var1);
}
