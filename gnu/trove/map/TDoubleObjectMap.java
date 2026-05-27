package gnu.trove.map;

public interface TDoubleObjectMap {
   double getNoEntryKey();

   int size();

   boolean containsKey(double var1);

   Object get(double var1);

   boolean forEachEntry(gnu.trove.procedure.TDoubleObjectProcedure var1);
}
