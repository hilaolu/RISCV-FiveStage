package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {

    
        
    val io = IO(
        new Bundle {
            val in=new Bundle{
                val alu_data=Input(UInt(32.W))
                val rd=Input(UInt(5.W))
                val w_rd=Input(Bool())
                
                val mem_op=Input(Bool())
                val mem_data=Input(UInt(32.W))
                
            }
            
            val out=Output(new WBRegs.Contents)
            val mem_data=Output(UInt(32.W))
    })

    val DMEM = Module(new DMEM)

    DMEM.io.dataIn      := io.in.mem_data 
    DMEM.io.dataAddress := io.in.alu_data 
    DMEM.io.writeEnable := io.in.mem_op&&(~io.in.w_rd)
    
    io.mem_data:=DMEM.io.dataOut
    io.out.reg_data:=io.in.alu_data
    
    io.out.rd:=io.in.rd
    io.out.w_rd:=io.in.w_rd
    io.out.mem_op:=io.in.mem_op
    
    // Don't touch the test harness
    val testHarness = IO(
        new Bundle {
            val DMEMsetup      = Input(new DMEMsetupSignals)
            val DMEMpeek       = Output(UInt(32.W))
            
            val testUpdates    = Output(new MemUpdates)
    })

    DMEM.testHarness.setup  := testHarness.DMEMsetup
    testHarness.DMEMpeek    := DMEM.io.dataOut
    testHarness.testUpdates := DMEM.testHarness.testUpdates
}
