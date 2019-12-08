// === Cadmium Headers ===
#include <cadmium/modeling/ports.hpp>
#include <cadmium/modeling/dynamic_model.hpp>
#include <cadmium/modeling/dynamic_model_translator.hpp>
#include <cadmium/engine/pdevs_dynamic_runner.hpp>
#include <cadmium/logger/common_loggers.hpp>
#include <cadmium/basic_model/pdevs/iestream.hpp> //Atomic model for inputs

// === Standard C++ Libraries===
#include <iostream>
#include <string>

// === Time Library ===
#include "EIRational.hpp"

// === Additional Headers ===
#include <boost/algorithm/string.hpp>
#include "../../messages/StratAirLiftSimMessage.hpp"
#include "EIRational.hpp"


// === Atomic Model Headers ===

#include "../../atomics/Aircraft.hpp"

#include "../../atomics/Location.hpp"


using namespace std;
using namespace cadmium;
using namespace cadmium::basic_models::pdevs;

using TIME = EIRational;

// === Port Configs ===
struct AC_inLoads : public in_port<oLoad>{};
struct LOC_InputACStatus : public in_port<oAircraftStatus>{};
struct LOC_InputLoads : public in_port<oLoad>{};
struct LOC_InputLocInfo : public in_port<oLocInfo>{};
struct LOC_InputPallets : public in_port<oPallet>{};
struct AC_outLoads : public out_port<oLoad>{};
struct AC_outStatus : public out_port<oAircraftStatus>{};
struct LOC_outLoads : public out_port<oLoad>{};
struct LOC_outPallets : public out_port<oPallet>{};
struct outLoads : public out_port<oLoad>{};
struct outPallets : public out_port<oPallet>{};


// === Input Readers ===

template<typename T>
class InputReader_oLoad : public iestream_input<oLoad,T> {
    public:
        InputReader_oLoad () = default;
        InputReader_oLoad (const char* file_path) : iestream_input<oLoad,T>(file_path) {}
};
template<typename T>
class InputReader_oACStatus : public iestream_input<oAircraftStatus,T> {
    public:
        InputReader_oACStatus () = default;
        InputReader_oACStatus (const char* file_path) : iestream_input<oAircraftStatus,T>(file_path) {}
};
template<typename T>
class InputReader_oLocInfo : public iestream_input<oLocInfo,T> {
    public:
        InputReader_oLocInfo () = default;
        InputReader_oLocInfo (const char* file_path) : iestream_input<oLocInfo,T>(file_path) {}
};
template<typename T>
class InputReader_oPallet : public iestream_input<oPallet,T> {
    public:
        InputReader_oPallet () = default;
        InputReader_oPallet (const char* file_path) : iestream_input<oPallet,T>(file_path) {}
};


int main(int argc, char *argv[])
{
	// === Atomic Models ===
	
	
	string myirLoadInInFile = "LocationTest_Loads.txt";
    const char * sirLoadInInFile = myirLoadInInFile.c_str();
    shared_ptr<dynamic::modeling::model> irLoadIn = dynamic::translate::make_dynamic_atomic_model<InputReader_oLoad , TIME, const char* >("irLoadIn" , move(sirLoadInInFile));
	
	string myirACStatusInInFile = "LocationTest_ACStatus.txt";
    const char * sirACStatusInInFile = myirACStatusInInFile.c_str();
    shared_ptr<dynamic::modeling::model> irACStatusIn = dynamic::translate::make_dynamic_atomic_model<InputReader_oACStatus , TIME, const char* >("irACStatusIn" , move(sirACStatusInInFile));
	
	string myirPalletInInFile = "LocationTest_Pallets.txt";
    const char * sirPalletInInFile = myirPalletInInFile.c_str();
    shared_ptr<dynamic::modeling::model> irPalletIn = dynamic::translate::make_dynamic_atomic_model<InputReader_oPallet , TIME, const char* >("irPalletIn" , move(sirPalletInInFile));
	
	string myirLocInfoInInFile = "LocationTest_LocInfo.txt";
    const char * sirLocInfoInInFile = myirLocInfoInInFile.c_str();
    shared_ptr<dynamic::modeling::model> irLocInfoIn = dynamic::translate::make_dynamic_atomic_model<InputReader_oLocInfo , TIME, const char* >("irLocInfoIn" , move(sirLocInfoInInFile));
	shared_ptr<dynamic::modeling::model> Trenton=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Trenton",1,"Trenton",44.1,-77.6);
	
	shared_ptr<dynamic::modeling::model> Paris=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Paris",2,"Paris",48.9,2.4);
	
	shared_ptr<dynamic::modeling::model> Iceland=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Iceland",3,"Iceland",50.5,30.5);
	
	shared_ptr<dynamic::modeling::model> Congo=dynamic::translate::make_dynamic_atomic_model<Location, TIME,int,string,double,double>("Congo",4,"Congo",4.3,-15.3);
	
	shared_ptr<dynamic::modeling::model> Aircraft1=dynamic::translate::make_dynamic_atomic_model<Aircraft, TIME,int,int,int,int>("Aircraft1",1,1,1,360);
	
	shared_ptr<dynamic::modeling::model> Aircraft2=dynamic::translate::make_dynamic_atomic_model<Aircraft, TIME,int,int,int,int>("Aircraft2",2,1,1,360);
	
    


	
	// === Locations Coupled Models ===
    dynamic::modeling::Ports iports_Locations = {typeid(LOC_InputLoads),typeid(LOC_InputACStatus),typeid(LOC_InputPallets),typeid(LOC_InputLocInfo)};
    dynamic::modeling::Ports oports_Locations = {typeid(LOC_outLoads),typeid(LOC_outPallets)};
    
	dynamic::modeling::Models submodels_Locations = {Trenton,Paris,Iceland,Congo};
	
	dynamic::modeling::EICs eics_Locations = {
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Trenton"),
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Paris"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Paris"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Paris"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Paris"),
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Iceland"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Iceland"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Iceland"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Iceland"),
		dynamic::translate::make_EIC<LOC_InputLoads, Locations_defs::inLoads>("Congo"),
		dynamic::translate::make_EIC<LOC_InputACStatus, Locations_defs::inACStatus>("Congo"),
		dynamic::translate::make_EIC<LOC_InputPallets, Locations_defs::inPallets>("Congo"),
		dynamic::translate::make_EIC<LOC_InputLocInfo, Locations_defs::inLocInfo>("Congo")
	};

	dynamic::modeling::EOCs eocs_Locations = {
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Trenton"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Trenton"),
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Paris"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Paris"),
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Iceland"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Iceland"),
		dynamic::translate::make_EOC<Locations_defs::outLoads, LOC_outLoads>("Congo"),
		dynamic::translate::make_EOC<Locations_defs::outPallets, LOC_outPallets>("Congo")
	};

	dynamic::modeling::ICs ics_Locations = {};

    shared_ptr<dynamic::modeling::coupled<TIME>> Locations;
    Locations = make_shared<dynamic::modeling::coupled<TIME>>(
        "Locations", submodels_Locations, iports_Locations, oports_Locations, eics_Locations, eocs_Locations, ics_Locations
    );
	
	// === Aircraft Coupled Models ===
    dynamic::modeling::Ports iports_Aircraft = {typeid(AC_inLoads)};
    dynamic::modeling::Ports oports_Aircraft = {typeid(AC_outLoads),typeid(AC_outStatus)};
    
	dynamic::modeling::Models submodels_Aircraft = {Aircraft1,Aircraft2};
	
	dynamic::modeling::EICs eics_Aircraft = {
		dynamic::translate::make_EIC<AC_inLoads, Aircraft_defs::inLoads>("Aircraft1"),
		dynamic::translate::make_EIC<AC_inLoads, Aircraft_defs::inLoads>("Aircraft2")
	};

	dynamic::modeling::EOCs eocs_Aircraft = {
		dynamic::translate::make_EOC<Aircraft_defs::outLoads, AC_outLoads>("Aircraft1"),
		dynamic::translate::make_EOC<Aircraft_defs::outACStatus, AC_outStatus>("Aircraft1"),
		dynamic::translate::make_EOC<Aircraft_defs::outLoads, AC_outLoads>("Aircraft2"),
		dynamic::translate::make_EOC<Aircraft_defs::outACStatus, AC_outStatus>("Aircraft2")
	};

	dynamic::modeling::ICs ics_Aircraft = {};

    shared_ptr<dynamic::modeling::coupled<TIME>> Aircraft;
    Aircraft = make_shared<dynamic::modeling::coupled<TIME>>(
        "Aircraft", submodels_Aircraft, iports_Aircraft, oports_Aircraft, eics_Aircraft, eocs_Aircraft, ics_Aircraft
    );
	
	// === TOP Coupled Models ===
    dynamic::modeling::Ports iports_TOP = {};
    dynamic::modeling::Ports oports_TOP = {typeid(outLoads),typeid(outPallets)};
    
	dynamic::modeling::Models submodels_TOP = {irLoadIn,irACStatusIn,irPalletIn,irLocInfoIn,Locations,Aircraft};
	
	dynamic::modeling::EICs eics_TOP = {};

	dynamic::modeling::EOCs eocs_TOP = {
		dynamic::translate::make_EOC<LOC_outLoads, outLoads>("Locations"),
		dynamic::translate::make_EOC<LOC_outPallets, outPallets>("Locations")
	};

	dynamic::modeling::ICs ics_TOP = {
		dynamic::translate::make_IC<LOC_outLoads, AC_inLoads>("Locations","Aircraft"),
		dynamic::translate::make_IC<AC_outStatus, LOC_InputACStatus>("Aircraft","Locations"),
		dynamic::translate::make_IC<AC_outLoads, LOC_InputLoads>("Aircraft","Locations"),
		dynamic::translate::make_IC<iestream_input_defs<oLoad>::out, LOC_InputLoads>("irLoadIn","Locations"),
		dynamic::translate::make_IC<iestream_input_defs<oAircraftStatus>::out, LOC_InputACStatus>("irACStatusIn","Locations"),
		dynamic::translate::make_IC<iestream_input_defs<oPallet>::out, LOC_InputPallets>("irPalletIn","Locations"),
		dynamic::translate::make_IC<iestream_input_defs<oLocInfo>::out, LOC_InputLocInfo>("irLocInfoIn","Locations")
	};

    shared_ptr<dynamic::modeling::coupled<TIME>> TOP;
    TOP = make_shared<dynamic::modeling::coupled<TIME>>(
        "TOP", submodels_TOP, iports_TOP, oports_TOP, eics_TOP, eocs_TOP, ics_TOP
    );
	
	
		
	// === Loggers ===

	static ofstream out_stratairliftjson("stratairliftjson.txt");
	struct oss_stratairliftjson{
		static ostream& sink(){
			return out_stratairliftjson;
		}
	};


    using state=logger::logger<logger::logger_state, dynamic::logger::formatter<TIME>, oss_stratairliftjson>;

    using logger_top=logger::multilogger<state>;

	// === Runner Call ===
    dynamic::engine::runner<TIME, logger_top> r(TOP, {0});
    
    r.run_until(TIME(1000));
    
    

    return 0;
}
