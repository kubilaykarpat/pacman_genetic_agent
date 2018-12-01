package internal_competition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.util.Stats;

public class tournament {

	private static final String FILENAME = "C:\\Users\\dockhorn\\Desktop\\Pacman Einreichungen\\auswertung.txt";
    private static POCommGhosts poghosts = new POCommGhosts();
    private static PacmanController randompacman = new entrants.pacman.ADockhorn.RandomJunctionPacMan();
	
	@SuppressWarnings("unchecked")
	public static double test_against_starter_ghosts(Class pac, int tests){
		Executor po = new Executor(true, true, true);
        po.setDaemon(true);	

        try {
			Stats[] stats_player = po.runExperiment((Controller<MOVE>) pac.newInstance(), poghosts, tests, "test");


			return  stats_player[0].getAverage();
			
        } catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return 0d;
	}
	
	public static double test_against_starter_pacman(Class ghosts, int tests){
		Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        try {
			Stats[] stats_player = po.runExperiment(randompacman, (MASController) ghosts.newInstance(), tests, "test");
			return  stats_player[0].getAverage();
        } catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return 0d;
	}

	
	
		
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static double[][] tournament_mode(List<Class> pacmans, List<Class> ghosts, int tests, Logger logger) throws IOException
	{
		Executor po = new Executor(true, true, true);
        po.setDaemon(true);	
        
		Class pac;
		Class gho; 
		double[][] values = new double[pacmans.size()][ghosts.size()];
		Stats[] stats;
		
			FileWriter fw = new FileWriter(FILENAME);
			BufferedWriter bw = new BufferedWriter(fw);
			logger.info("#Pacman; #Ghost; Score\n");
			
			for(int i = 0; i < pacmans.size(); i++){
				pac = pacmans.get(i);
				
				for (int j = 0; j < ghosts.size(); j++){
					gho = ghosts.get(j);
					
					try {
						stats = po.runExperiment((Controller<MOVE>) pac.newInstance(), (MASController) gho.newInstance(), tests, "");
						logger.info("" + i + "; " + j + "; " + stats[0].getAverage() + "\n");
						values[i][j] = stats[0].getAverage();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						logger.info("" + i + "; " + j + "; -1 \n");
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						logger.info("" + i + "; " + j + "; -1 \n");
					}
				}
			}
		return values;
	}
	
	public static void init_tournament(String folder) throws IOException{
		
		Logger logger = Logger.getLogger("MyLog");
	    FileHandler fh;  

		// try to store everything into a file
		String filename = "results.log";
		File yourFile = new File(filename);
		
		// create the logging file if it not exists
		try {
			yourFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {  
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(filename);  
	        logger.addHandler(fh);
			SimplestFormatter formatter = new SimplestFormatter();
	        fh.setFormatter(formatter);  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
		
		
		LinkedList<Class> pacmans = new LinkedList<Class>();
		
		pacmans.add(internal_competition.entrants.pacman.team1.MyPacMan.class);
		pacmans.add(internal_competition.entrants.pacman.team2.ArnesPacmanController.class);
		pacmans.add(internal_competition.entrants.pacman.team3.Jin42PacMan.class);
		pacmans.add(internal_competition.entrants.pacman.team4.MyPacMan.class);
		pacmans.add(internal_competition.entrants.pacman.team5.MTDfBot.class);
		pacmans.add(entrants.pacman.username.MyPacMan.class);
		pacmans.add(entrants.pacman.dweikert.MyPacMan.class);
		pacmans.add(entrants.pacman.felix.MyPacMan.class);
		pacmans.add(entrants.pacman.aristocat.CatMan.class);
		pacmans.add(entrants.pacman.Imad_Hajjar.Pacman.class);
		pacmans.add(internal_competition.entrants.pacman.team11.RollingRodeo.class);
		//pacmans.add(entrants.pacman.kenneth.MyPacMan.class);
		pacmans.add(entrants.pacman.gzae.MyPacMan.class);
		pacmans.add(entrants.pacman.BreakingPac.MyPacMan.class);
		//pacmans.add(entrants.pacman.max_frick.MyPacMan.class);
		pacmans.add(entrants.pacman.hoque.MyPacMan.class);
		//pacmans.add(entrants.pacman.scalingocto.ScalingOctoPacman.class);
		pacmans.add(entrants.pacman.crocodilecookies.PacMan.class);
		pacmans.add(entrants.pacman.nstt.MyPacMan.class);
		//pacmans.add(entrants.pacman.towunder.MyPacMan.class);
		pacmans.add(entrants.pacman.antolyt.MyPacMan.class);

		
		pacmans.add(entrants.pacman.ADockhorn.RandomJunctionPacMan.class);
		pacmans.add(examples.StarterPacMan.MyPacMan.class);

		
		
		List<Class> ghosts = new LinkedList<Class>();
		ghosts.add(internal_competition.entrants.ghosts.team1.Ghosts.class);
		ghosts.add(entrants.ghosts.username.Ghosts.class);
		ghosts.add(entrants.ghosts.dweikert.Ghosts.class);
		ghosts.add(entrants.ghosts.felix.POCommGhost.class);
		ghosts.add(entrants.ghosts.aristocat.CatGhosts.class);
		ghosts.add(entrants.ghosts.Imad_Hajjar.MyGhosts.class);
		//ghosts.add(entrants.ghosts.kenneth.Ghosts.class);
		ghosts.add(entrants.ghosts.gzae.Ghosts.class);
		ghosts.add(entrants.ghosts.BreakingPac.Ghosts.class);
		ghosts.add(entrants.ghosts.max_frick.Ghosts.class);
		ghosts.add(entrants.ghosts.hoque.MyGhosts.class);
		ghosts.add(entrants.ghosts.nstt.Ghosts.class);
		//ghosts.add(entrants.ghosts.towunder.Ghosts.class);
		ghosts.add(entrants.ghosts.antolyt.Ghosts.class);

		double[][] test = tournament_mode(pacmans, ghosts, 1, logger);
	}
	
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException{
		Executor po = new Executor(true, true, true);
        po.setDaemon(true);	

        //init_tournament(FILENAME);	
        
        
        /*
        int tests = 100;
        
		Stats[] stats_random = po.runExperiment(new entrants.pacman.ADockhorn.RandomJunctionPacMan(), poghosts, tests, "test");
		Stats[] stats_starter = po.runExperiment(new examples.StarterPacMan.MyPacMan(), poghosts, tests, "test");

		double points = test_against_starter_ghosts(entrants.pacman.dweikert.MyPacMan.class, tests);
		
		int p = 0;
		for (int i = 0; i < tests; i++){
			Stats[] single_run = po.runExperiment(new entrants.pacman.dweikert.MyPacMan(), poghosts, 1, "test");
			p += single_run[0].getAverage();
		}
		
		System.out.println(stats_random[0].getAverage());
		System.out.println(stats_starter[0].getAverage());
		System.out.println(points);
		System.out.println(p/tests);
	
		*/
		System.out.println(test_against_starter_pacman(internal_competition.entrants.ghosts.team1.Ghosts.class, 10));
		//init_tournament("");
	}
}
