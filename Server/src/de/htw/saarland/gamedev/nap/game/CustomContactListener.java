package de.htw.saarland.gamedev.nap.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.skills.Axe;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;

public class CustomContactListener implements ContactListener {
	
	//Exceptions
	private static final String EXCEPTION_NULL_GAME = "The game object is null!";
	//TODO add constants for userdata
	
	private GameServer game;
	
	public CustomContactListener(GameServer game){
		if(game==null) throw new NullPointerException(EXCEPTION_NULL_GAME);
		
		this.game=game;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		//Axe hitting a player
				if(fA.getUserData()!=null && fB.getUserData()!= null){
					if(fA.getUserData()==Axe.USERDATA_AXE && fB.getUserData()==PlayableCharacter.USERDATA_PLAYER){
						for(Player p: game.teamBlue){
							if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Axe.DAMAGE);
						}
						for(Player p: game.teamRed){
							if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Axe.DAMAGE);
						}
					}
					if(fB.getUserData()==Axe.USERDATA_AXE && fA.getUserData()==PlayableCharacter.USERDATA_PLAYER){
						for(Player p: game.teamBlue){
							if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Axe.DAMAGE);
						}
						for(Player p: game.teamRed){
							if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Axe.DAMAGE);
						}
					}
				}
		
		//Fireball hitting the world
		if(fA.getUserData()!=null && fB.getUserData()!= null){
			if(fA.getUserData()==Fireball.USERDATA_FIREBALL && fB.getUserData()==GameWorld.USERDATA_FIXTURE_WORLD){
				fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			if(fB.getUserData()==Fireball.USERDATA_FIREBALL && fA.getUserData()==GameWorld.USERDATA_FIXTURE_WORLD){
				fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
		}
		
		//Fireball hitting a player
		if(fA.getUserData()!=null && fB.getUserData()!= null){
			if(fA.getUserData()==Fireball.USERDATA_FIREBALL && fB.getUserData()==PlayableCharacter.USERDATA_PLAYER){
				for(Player p: game.teamBlue){
					if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Fireball.DAMAGE);
				}
				for(Player p: game.teamRed){
					if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Fireball.DAMAGE);
				}
				fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			if(fB.getUserData()==Fireball.USERDATA_FIREBALL && fA.getUserData()==PlayableCharacter.USERDATA_PLAYER){
				for(Player p: game.teamBlue){
					if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Fireball.DAMAGE);
				}
				for(Player p: game.teamRed){
					if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setHealth(p.getPlChar().getHealth()-Fireball.DAMAGE);
				}
				fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
		}
		
		//spawnPoint team blue
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			for(Player p:game.teamBlue){
				if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setAtSpawn(true);
			}
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			for(Player p:game.teamBlue){
				if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setAtSpawn(true);
			}
		}
		//spawnPoint team red
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			for(Player p:game.teamRed){
				if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setAtSpawn(true);
			}
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			for(Player p:game.teamRed){
				if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setAtSpawn(true);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		if(fA!=null && fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			for(Player p:game.teamBlue){
				if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setAtSpawn(false);
			}
		}
		if(fB!=null && fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			for(Player p:game.teamBlue){
				if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setAtSpawn(false);
			}
		}
		//spawnPoint team red
		if(fA!=null && fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			for(Player p:game.teamRed){
				if(p.getPlChar().getFixture().equals(fB)) p.getPlChar().setAtSpawn(false);
			}
		}
		if(fB!=null && fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			for(Player p:game.teamRed){
				if(p.getPlChar().getFixture().equals(fA)) p.getPlChar().setAtSpawn(false);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//TODO remove keypress argument
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		float offsetA = 0;
		float offsetB = 0;
		PolygonShape playerShape;
		Vector2 tmpVector = new Vector2();
		//calculate y offset
		if(fA.getUserData()!=null && fA.getUserData()==PlayableCharacter.USERDATA_PLAYER){
			playerShape=(PolygonShape) fA.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetA = tmpVector.y;
		}
		if(fB.getUserData()!=null && fB.getUserData()==PlayableCharacter.USERDATA_PLAYER){
			playerShape= (PolygonShape) fB.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetB = tmpVector.y;
		}
		
		
		
		//player jumping through a platform from below
		if(fA.getUserData()!=null && (fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) || fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO))){
			if(fB.getBody().getPosition().y+offsetB<fA.getBody().getPosition().y){
				contact.setEnabled(false);
			}
		}
		if(fB.getUserData()!=null && (fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) || fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO))){
			if(fA.getBody().getPosition().y+offsetA<fB.getBody().getPosition().y){
				contact.setEnabled(false);
				}
		}
		//player dropping through a platform from above
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)){
			if((Math.abs(fB.getBody().getPosition().y+offsetB-fA.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S)){
				for(Player p:game.teamBlue){
					if (fB.equals(p.getPlChar().getFixture()) && p.getPlChar().getTimeOnGround()>=p.getPlChar().MIN_TIME_ON_GROUND)
						contact.setEnabled(false);
				}
				for(Player p:game.teamRed){
					if (fB.equals(p.getPlChar().getFixture()) && p.getPlChar().getTimeOnGround()>=p.getPlChar().MIN_TIME_ON_GROUND)
						contact.setEnabled(false);
				}
			}
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)){
			if((Math.abs(fA.getBody().getPosition().y-offsetA-fB.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S)){
				if((Math.abs(fB.getBody().getPosition().y+offsetB-fA.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S)){
					for(Player p:game.teamBlue){
						if (fA.equals(p.getPlChar().getFixture()) && p.getPlChar().getTimeOnGround()>=p.getPlChar().MIN_TIME_ON_GROUND)
							contact.setEnabled(false);
					}
					for(Player p:game.teamRed){
						if (fA.equals(p.getPlChar().getFixture()) && p.getPlChar().getTimeOnGround()>=p.getPlChar().MIN_TIME_ON_GROUND)
							contact.setEnabled(false);
					}
				}
			}
		}			
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
