package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;

final class THashMap$EntryView$EntryIterator extends TObjectHashIterator {
   // $FF: synthetic field
   final THashMap$EntryView this$1;

   THashMap$EntryView$EntryIterator(THashMap$EntryView var1, THashMap var2) {
      super(var2);
      this.this$1 = var1;
   }

   public final THashMap$Entry objectAtIndex(int var1) {
      return new THashMap$Entry(THashMap$EntryView.access$2(this.this$1), THashMap$EntryView.access$2(this.this$1)._set[var1], THashMap$EntryView.access$2(this.this$1)._values[var1], var1);
   }
}
