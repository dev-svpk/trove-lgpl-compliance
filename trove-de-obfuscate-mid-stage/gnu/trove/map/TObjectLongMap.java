package gnu.trove.map;

public interface TObjectLongMap {
   long getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   long get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectLongProcedure var1);
}
