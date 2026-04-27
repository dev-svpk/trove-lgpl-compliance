package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
class TShortObjectHashMap$ValueView$1 extends TShortObjectHashMap$ValueView$TShortObjectValueHashIterator {
   // $FF: synthetic field
   final TShortObjectHashMap$ValueView this$1;

   TShortObjectHashMap$ValueView$1(TShortObjectHashMap$ValueView var1, TShortObjectHashMap$ValueView var2, TShortObjectHashMap var3) {
      super(var1, var3);
      this.this$1 = var2;
   }

   protected Object objectAtIndex(int var1) {
      return TShortObjectHashMap$ValueView.access$0(this.this$1)._values[var1];
   }
}
