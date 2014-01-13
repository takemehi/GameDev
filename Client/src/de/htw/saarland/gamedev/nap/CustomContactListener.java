package de.htw.saarland.gamedev.nap;

import java.util.List;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.client.entity.ClientPlayer;
import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.IPlayer;
import de.htw.saarland.gamedev.nap.data.Platform;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.skills.Axe;
import de.htw.saarland.gamedev.nap.data.skills.Charge;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;
import de.htw.saarland.gamedev.nap.data.skills.Nova;
import de.htw.saarland.gamedev.nap.data.skills.Pyroblast;
import de.htw.saarland.gamedev.nap.data.skills.Snare;

public class CustomContactListener implements ContactListener {

	private Array<IPlayer> players;
	private Array<CapturePoint> capturePoints;

	public CustomContactListener(List<ClientPlayer> players, Array<CapturePoint> capturePoints) {
		this.capturePoints=capturePoints;
		for(IPlayer p: players){
			this.players.add(p);
		}

	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();

		if (fA.getUserData() != null && fB.getUserData() != null) {
			//Character abilities
			Axe.handleContact(fA, fB, players, true);
			Snare.handleContact(fA, fB, players, true);
			Charge.handleContact(fA, fB, players, true);
			Fireball.handleContact(fA, fB, players, true);
			Pyroblast.handleContact(fA, fB, players, true);
			Nova.handleContact(fA, fB, players, true);
			
			//Capture- and spawn point
			//CapturePoint.handleContactBegin(fA, fB, players, capturePoints);
			//SpawnPoint.handleContactBegin(fA, fB, blueTeam, redTeam);
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		if(fA!=null && fB!=null){
			//CapturePoint.handleContactEnd(fA, fB, players, capturePoints);
			//SpawnPoint.handleContactEnd(fA, fB, blueTeam, redTeam);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Platform.handleContactPreSolve(contact, players);		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
