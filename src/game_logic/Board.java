package game_logic;

import java.util.List;
import java.util.ArrayList;

public class Board {

	private List<List<Tile>> tiles;
	
	public Board(){
		tiles = new ArrayList<List<Tile>>();
		for(int i=0;i<25;i++) {
			List<Tile> row = new ArrayList<Tile>();
			tiles.add(row);
			for(int j=0;j<24;j++) {
				tiles.get(i).add(new Tile(i,j));
			}
		}
		fillInvalid();
		fillNeighbours();
	}

	public List<List<Tile>> getTiles() {
		return tiles;
	}
	
	public void printBoard() {
		
		for(int i=0;i<25;i++) {			
			for(int j=0;j<24;j++) {
				if(i==8 && j==4)
					System.out.print("[A]");
				else if(tiles.get(8).get(4).getNeighbours().contains(tiles.get(i).get(j).getCoordinates()))
					System.out.print("[N]");
				else
					tiles.get(i).get(j).printTile();
			}
			System.out.println("\n");
		}
	}
	
	private void fillInvalid() {
		for(int i=0;i<25;i++){			
			tiles.get(i).get(0).setValid(false);
			tiles.get(i).get(23).setValid(false);
		}
		for(int i=0;i<24;i++){			
			tiles.get(0).get(i).setValid(false);
			tiles.get(24).get(i).setValid(false);
		}
		
		//starting points
		//mudar para jogadores
		tiles.get(0).get(9).setValid(true);
		tiles.get(0).get(14).setValid(true);
		tiles.get(24).get(7).setValid(true);
		tiles.get(24).get(16).setValid(true);
		tiles.get(7).get(0).setValid(true);
		tiles.get(17).get(0).setValid(true);
		tiles.get(19).get(23).setValid(true);
		tiles.get(6).get(23).setValid(true);
		
		//rooms
		//kitchen
		fillRoom(1,6,1,5);
		tiles.get(1).get(6).setValid(false);
		tiles.get(6).get(4).setDoor("Kitchen");
		
		//dining room
		for(int i=1; i<5;i++)
			tiles.get(9).get(i).setValid(false);
		fillRoom(10,15,1,7);
		tiles.get(15).get(6).setDoor("Dining Room");
		tiles.get(12).get(7).setDoor("Dining Room");
		
		//lounge
		fillRoom(19,24,1,6);
		tiles.get(19).get(6).setDoor("Lounge");
		
		//hall
		fillRoom(18,24,9,14);
		tiles.get(18).get(11).setDoor("Hall");
		tiles.get(18).get(12).setDoor("Hall");
		tiles.get(20).get(14).setDoor("Hall");
		
		//study
		fillRoom(21,23,17,22);
		tiles.get(21).get(17).setDoor("Study");
		
		//library
		fillRoom(14,18,18,22);
		tiles.get(15).get(17).setValid(false);
		tiles.get(16).get(17).setDoor("Library");
		tiles.get(17).get(17).setValid(false);
		tiles.get(14).get(21).setDoor("Library");
		
		//billiard room
		fillRoom(8,12,18,22);
		tiles.get(9).get(18).setDoor("Billiard Room");
		
		//conservatory
		fillRoom(1,5,18,22);
		tiles.get(5).get(18).setValid(true);
		tiles.get(4).get(18).setDoor("Conservatory");
		tiles.get(1).get(17).setValid(false);
		
		//ball room
		fillRoom(2,7,8,15);	
		tiles.get(1).get(10).setValid(false);
		tiles.get(1).get(11).setValid(false);
		tiles.get(1).get(12).setValid(false);
		tiles.get(1).get(13).setValid(false);
		tiles.get(7).get(9).setDoor("Ballroom");
		tiles.get(7).get(14).setDoor("Ballroom");
		tiles.get(5).get(8).setDoor("Ballroom");
		tiles.get(5).get(15).setDoor("Ballroom");
		
		//middle
		fillRoom(10,16,10,14);		
		
	
	}

	private void fillRoom(int x1,int x2,int y1,int y2) {
		for(int i=x1;i<x2+1;i++)
			for(int j=y1;j<y2+1;j++)
				tiles.get(i).get(j).setValid(false);
	}
	
	private void fillNeighbours() {
		for(int i=0;i<25;i++) {
			for(int j=0;j<24;j++) {
				Tile currentTile = tiles.get(i).get(j);

				if((j+1)<24 && tiles.get(i).get(j+1).isValid()) //East
					currentTile.addNeighbour(new Coordinates(i,j+1));
				if((i+1)<25 && tiles.get(i+1).get(j).isValid()) //South
					currentTile.addNeighbour(new Coordinates(i+1,j));
				if((j-1)>-1 && tiles.get(i).get(j-1).isValid()) //West
					currentTile.addNeighbour(new Coordinates(i,j-1));
				if((i-1)>-1 && tiles.get(i-1).get(j).isValid()) //North
					currentTile.addNeighbour(new Coordinates(i-1,j));	
			}
		}
	}
}