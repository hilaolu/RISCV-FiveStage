package FiveStage
import chisel3.core.ExplicitCompileOptions.Strict
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

import lookup._

object InstructionFetch {
    class Jump extends Bundle{
        val jump_addr=UInt(30.W)
        val e_jump=Bool()
    }
}
    



class InstructionFetch extends MultiIOModule {


    val io = IO(new Bundle {
        val stall=Input(Bool())
        // val e_branch=input(bool())
        // val branch_offset=input(uint(30.w))
        
        
        val pc = Output(UInt(30.W))
        val pc_4=Output(UInt(30.W))
        val ins= Output(new Instruction) 
        
        
        
        val decode_jump=Input(new InstructionFetch.Jump)
    })
    
    val IMEM = Module(new IMEM)
    val s_pc = RegInit(UInt(30.W),(-1).S.asTypeOf(SInt(30.W)).asUInt)
    val adder_result=Wire(UInt(30.W))
    val current_pc=RegInit(UInt(30.W),0.U)
    val offset=Wire(UInt(30.W))
    
    //addr is 30 bit!!
    
    io.pc := current_pc 
    io.pc_4 := adder_result//fix me 
    
    IMEM.io.instructionAddress := Cat(Mux(io.stall,current_pc,s_pc),0.U(2.W))
    val ins=IMEM.io.instruction.asTypeOf(new Instruction)
    io.ins:=ins
    
    val d_pc=Wire(UInt(30.W))
    
    d_pc:=s_pc
    when(io.stall){
        d_pc:=current_pc
    }
    
    current_pc:=s_pc
    when(io.stall){
        current_pc:=current_pc
    }
    
    adder_result := s_pc+offset 
    
    offset:=1.U//fix me
    
    s_pc:=adder_result
    when(io.stall){
        s_pc:=s_pc
    }
    
    
    
    // when(ins.asUInt===JAL){
    //     current_pc:=Cat(current_pc+ins.immediateJType(19,2))
    // }
    // when(io.decode_jump.e_jump){
    //     current_pc:=io.decode_jump.jump_addr
    // }
        
    
    /**
    * Setup. You should not change this code
    */
    val testHarness = IO(
        new Bundle {
            val IMEMsetup = Input(new IMEMsetupSignals)
            val PC        = Output(UInt())
        }
    )
    
    IMEM.testHarness.setupSignals := testHarness.IMEMsetup
    testHarness.PC := IMEM.testHarness.requestedAddress
    when(testHarness.IMEMsetup.setup) {
        s_pc := 0.U
        ins := Instruction.NOP
    }
  
}





