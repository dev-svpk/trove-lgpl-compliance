package gnu.trove.map;

public interface TObjectIntMap {
   int getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   int get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectIntProcedure var1);
}
