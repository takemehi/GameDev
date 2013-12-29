package de.htw.saarland.gamedev.nap.data.platforms;

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

public class CustomContactListener implements ContactListener {
	//TODO add constants for userdata

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		//spawnPoint team blue
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			//TODO implement
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			//TODO implement
		}
		//spawnPoint team red
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			//TODO implement
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			//TODO implement
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		//spawnPoint team blue
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			//TODO implement
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)){
			//TODO implement
		}
		//spawnPoint team red
		if(fA.getUserData()!=null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			//TODO implement
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)){
			//TODO implement
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//TODO remove keypress argument
		//TODO find a way to delay dropping through the platform
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		float offsetA = 0;
		float offsetB = 0;
		PolygonShape playerShape;
		Vector2 tmpVector = new Vector2();
		//calculate y offset
		if(fA.getUserData()!=null && fA.getUserData()=="player"){
			playerShape=(PolygonShape) fA.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetA = tmpVector.y;
		}
		if(fB.getUserData()!=null && fB.getUserData()=="player"){
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
			if((Math.abs(fB.getBody().getPosition().y+offsetB-fA.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S))
				contact.setEnabled(false);
		}
		if(fB.getUserData()!=null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)){
			if((Math.abs(fA.getBody().getPosition().y-offsetA-fB.getBody().getPosition().y))<=0.1 && Gdx.input.isKeyPressed(Keys.S))
				contact.setEnabled(false);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
