package gnu.trove.map;

public interface TByteFloatMap {
   float getNoEntryValue();

   float get(byte var1);

   int size();

   gnu.trove.iterator.TByteFloatIterator iterator();
}
