package com.cdm.view.elements;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.cdm.view.IRenderer;
import com.cdm.view.PolySprite;
import com.cdm.view.Position;
import com.cdm.view.SpriteReader;
import com.cdm.view.elements.units.PlayerUnit;
import com.cdm.view.elements.units.Upgrade;

public class UpgradeView implements Element {
	private boolean visible = false;
	private Position position = new Position(0, 0, Position.LEVEL_REF);
	private Position tmpPos = new Position(0, 0, Position.LEVEL_REF);
	private PolySprite highlight = SpriteReader
			.read("/com/cdm/view/elements/units/highlight.sprite");
	private Upgrade selectedUpgrade = null;
	private PlayerUnit targetUnit;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (!visible)
			selectedUpgrade = null;
	}

	public PlayerUnit getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(PlayerUnit targetUnit) {
		this.targetUnit = targetUnit;
	}

	@Override
	public int compareTo(Element arg0) {
		return arg0.hashCode() - this.hashCode();
	}

	@Override
	public void draw(IRenderer renderer) {

	}

	@Override
	public void setPosition(Position pos) {
		position.set(pos);
	}

	@Override
	public void move(float time) {

	}

	private interface UpgradeWithPosition {
		void run(Upgrade upgrade, float dx, float dy);
	}

	private void doWithAllUpgrades(UpgradeWithPosition callback) {
		if (visible && targetUnit != null) {

			List<Upgrade> upgrades = targetUnit.getPossibleUpgrades();
			float a = 3.1415f * 2 / upgrades.size();
			int i = 0;
			float radius = 1.0f;
			for (Upgrade u : upgrades) {
				i++;
				float dx = (float) Math.sin(i * a) * radius;
				float dy = (float) Math.cos(i * a) * radius;
				callback.run(u, dx, dy);

			}
		}
	}

	@Override
	public void drawAfter(final IRenderer renderer) {
		doWithAllUpgrades(new UpgradeWithPosition() {

			@Override
			public void run(Upgrade u, float dx, float dy) {
				tmpPos.set(getPosition().x + dx, getPosition().y + dy,
						Position.LEVEL_REF);
				if (targetUnit != null) {
					Integer level = u.getCurrentLevel();
					if (level != null) {

						renderer.render(u.getSprite(), tmpPos, 0.5f, 0.0f,
								GL10.GL_TRIANGLES);
						if (selectedUpgrade == u) {
							renderer.render(highlight, tmpPos, 0.5f, 0.0f,
									GL10.GL_TRIANGLES);
						}
						renderer.drawText(tmpPos.to(Position.SCREEN_REF),
								level.toString(), Color.WHITE);
						tmpPos.x += dx - 0.25f;
						tmpPos.y += dy + 0.25f;
						renderer.drawText(tmpPos.to(Position.SCREEN_REF), "$"
								+ u.getCostForNext(), Color.WHITE);
					}
				}
			}
		});
	}

	private Position getPosition() {
		return position;
	}

	public void hover(final Position dragPosition) {
		doWithAllUpgrades(new UpgradeWithPosition() {

			@Override
			public void run(Upgrade u, float dx, float dy) {
				tmpPos.set(getPosition().x + dx, getPosition().y + dy,
						Position.LEVEL_REF);
				float dx2 = dragPosition.x - tmpPos.x;
				float dy2 = dragPosition.y - tmpPos.y;
				if (dx2 * dx2 + dy2 * dy2 < 1) {
					selectedUpgrade = u;
				}

			}
		});

	}

	public Upgrade getSelectedUpgrade() {
		return selectedUpgrade;
	}

}
