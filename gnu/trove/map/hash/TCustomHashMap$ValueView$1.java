package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;

class TCustomHashMap$ValueView$1 extends TObjectHashIterator {
   // $FF: synthetic field
   final TCustomHashMap$ValueView this$1;

   TCustomHashMap$ValueView$1(TCustomHashMap$ValueView var1, TObjectHash var2) {
      super(var2);
      this.this$1 = var1;
   }

   protected Object objectAtIndex(int var1) {
      return TCustomHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
