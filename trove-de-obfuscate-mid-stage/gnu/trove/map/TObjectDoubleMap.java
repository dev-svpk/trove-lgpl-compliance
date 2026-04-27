package gnu.trove.map;

public interface TObjectDoubleMap {
   double getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   double get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectDoubleProcedure var1);
}
