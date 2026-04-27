package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
class TIntObjectHashMap$ValueView$1 extends TIntObjectHashMap$ValueView$TIntObjectValueHashIterator {
   // $FF: synthetic field
   final TIntObjectHashMap$ValueView this$1;

   TIntObjectHashMap$ValueView$1(TIntObjectHashMap$ValueView var1, TIntObjectHashMap$ValueView var2, TIntObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TIntObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
