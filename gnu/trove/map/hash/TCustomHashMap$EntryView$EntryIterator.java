package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;

final class TCustomHashMap$EntryView$EntryIterator extends TObjectHashIterator {
   // $FF: synthetic field
   final TCustomHashMap$EntryView this$1;

   TCustomHashMap$EntryView$EntryIterator(TCustomHashMap$EntryView var1, TCustomHashMap var2) {
      super(var2);
      this.this$1 = var1;
   }

   public final TCustomHashMap$Entry objectAtIndex(int var1) {
      return new TCustomHashMap$Entry(TCustomHashMap$EntryView.access$2(this.this$1), TCustomHashMap$EntryView.access$2(this.this$1)._set[var1], TCustomHashMap$EntryView.access$2(this.this$1)._values[var1], var1);
   }
}
