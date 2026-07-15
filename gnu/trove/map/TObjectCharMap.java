package gnu.trove.map;

public interface TObjectCharMap {
   char getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   char get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectCharProcedure var1);
}
