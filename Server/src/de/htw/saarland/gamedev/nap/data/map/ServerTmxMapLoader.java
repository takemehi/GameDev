package de.htw.saarland.gamedev.nap.data.map;

import java.io.IOException;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader.Element;

public class ServerTmxMapLoader extends TmxMapLoader {
	public ServerTmxMapLoader() {
		super(new FileHandleResolver() {

			@Override
			public FileHandle resolve(String fileName) {
				return new FileHandle(fileName);
			}
		});
	}

	@Override
	public TiledMap load(String fileName, Parameters parameters) {
		try {
			this.yUp = parameters.yUp;
			this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
			FileHandle tmxFile = resolve(fileName);
			root = xml.parse(tmxFile);

			TiledMap map = loadTilemap(root, tmxFile, null);
			return map;
		} catch (IOException e) {
			throw new GdxRuntimeException("Couldn't load tilemap '" + fileName
					+ "'", e);
		}
	}

	@Override
	protected void loadTileSet(TiledMap map, Element element, FileHandle tmxFile, ImageResolver imageResolver) {
		if (element.getName().equals("tileset")) {
			String name = element.get("name", null);
			int firstgid = element.getIntAttribute("firstgid", 1);
			int tilewidth = element.getIntAttribute("tilewidth", 0);
			int tileheight = element.getIntAttribute("tileheight", 0);
			int spacing = element.getIntAttribute("spacing", 0);
			int margin = element.getIntAttribute("margin", 0);
			String source = element.getAttribute("source", null);

			String imageSource = "";
			int imageWidth = 0, imageHeight = 0;

			FileHandle image = null;
			if (source != null) {
				FileHandle tsx = getRelativeFileHandle(tmxFile, source);
				try {
					element = xml.parse(tsx);
					name = element.get("name", null);
					tilewidth = element.getIntAttribute("tilewidth", 0);
					tileheight = element.getIntAttribute("tileheight", 0);
					spacing = element.getIntAttribute("spacing", 0);
					margin = element.getIntAttribute("margin", 0);
					imageSource = element.getChildByName("image").getAttribute(
							"source");
					imageWidth = element.getChildByName("image")
							.getIntAttribute("width", 0);
					imageHeight = element.getChildByName("image")
							.getIntAttribute("height", 0);
					image = getRelativeFileHandle(tsx, imageSource);
				} catch (IOException e) {
					throw new GdxRuntimeException(
							"Error parsing external tileset.");
				}
			} else {
				imageSource = element.getChildByName("image").getAttribute(
						"source");
				imageWidth = element.getChildByName("image").getIntAttribute(
						"width", 0);
				imageHeight = element.getChildByName("image").getIntAttribute(
						"height", 0);
				image = getRelativeFileHandle(tmxFile, imageSource);
			}

			TiledMapTileSet tileset = new TiledMapTileSet();
			MapProperties props = tileset.getProperties();
			tileset.setName(name);
			props.put("firstgid", firstgid);
			props.put("imagesource", imageSource);
			props.put("imagewidth", imageWidth);
			props.put("imageheight", imageHeight);
			props.put("tilewidth", tilewidth);
			props.put("tileheight", tileheight);
			props.put("margin", margin);
			props.put("spacing", spacing);

			int stopWidth = imageWidth - tilewidth;
			int stopHeight = imageHeight - tileheight;

			int id = firstgid;

			for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
				for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
					
					TextureRegion n = null;
					TiledMapTile tile = new StaticTiledMapTile(n);
					tile.setId(id);
					tileset.putTile(id++, tile);
				}
			}

			Array<Element> tileElements = element.getChildrenByName("tile");

			for (Element tileElement : tileElements) {
				int localtid = tileElement.getIntAttribute("id", 0);
				TiledMapTile tile = tileset.getTile(firstgid + localtid);
				if (tile != null) {
					String terrain = tileElement.getAttribute("terrain", null);
					if (terrain != null) {
						tile.getProperties().put("terrain", terrain);
					}
					String probability = tileElement.getAttribute(
							"probability", null);
					if (probability != null) {
						tile.getProperties().put("probability", probability);
					}
					Element properties = tileElement
							.getChildByName("properties");
					if (properties != null) {
						loadProperties(tile.getProperties(), properties);
					}
				}
			}

			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(tileset.getProperties(), properties);
			}
			map.getTileSets().addTileSet(tileset);
		}
	}
}
