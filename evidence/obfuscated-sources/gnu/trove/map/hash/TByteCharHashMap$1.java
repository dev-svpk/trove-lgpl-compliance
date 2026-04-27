package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.b;

class TByteCharHashMap$1 implements b {
   private boolean first;
   // $FF: synthetic field
   final TByteCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TByteCharHashMap$1(TByteCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(byte var1, char var2) {
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
