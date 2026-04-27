package gnu.trove.map;

import gnu.trove.procedure.TShortObjectProcedure;

public interface TShortObjectMap {
   short getNoEntryKey();

   int size();

   boolean containsKey(short var1);

   Object get(short var1);

   boolean forEachEntry(TShortObjectProcedure var1);
}
