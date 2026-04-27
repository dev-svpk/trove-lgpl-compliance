package gnu.trove.map;

public interface TObjectFloatMap {
   float getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   float get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectFloatProcedure var1);
}
