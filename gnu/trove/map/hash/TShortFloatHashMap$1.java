package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TShortFloatProcedure;

class TShortFloatHashMap$1 implements TShortFloatProcedure {
   private boolean first;
   // $FF: synthetic field
   final TShortFloatHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TShortFloatHashMap$1(TShortFloatHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(short var1, float var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
