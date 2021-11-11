package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

object MEMRegs{
    class Contents extends Bundle{
        val alu_data=UInt(32.W)
        val rd=UInt(5.W)
        val w_rd=Bool()
        
        val mem_op=Bool()
        val mem_data=UInt(32.W)
    }
}

class MEMRegs extends MultiIOModule {
        
    val io = IO(
        new Bundle {
            val in=Input(new MEMRegs.Contents)            
            val out=Output(new MEMRegs.Contents)            
    })
    
    val contents=Reg(new MEMRegs.Contents)
    
    contents:=io.in
    io.out:=contents

}
