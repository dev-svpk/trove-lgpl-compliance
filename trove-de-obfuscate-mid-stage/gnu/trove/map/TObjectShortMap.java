package gnu.trove.map;

public interface TObjectShortMap {
   short getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   short get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectShortProcedure var1);
}
